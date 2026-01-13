package com.junoyi.framework.datasource.datascope.interceptor;

import com.junoyi.framework.datasource.datascope.DataScopeContextHolder;
import com.junoyi.framework.datasource.datascope.DataScopeContextHolder.DataScopeContext;
import com.junoyi.framework.datasource.datascope.DataScopeType;
import com.junoyi.framework.datasource.datascope.annotation.DataScope;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据范围 MyBatis 拦截器
 * <p>
 * 拦截查询语句，根据 @DataScope 注解自动添加数据范围过滤条件
 *
 * @author Fan
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class DataScopeInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取上下文
        DataScopeContext context = DataScopeContextHolder.get();

        // 无上下文或超级管理员，直接放行
        if (context == null || context.isSuperAdmin()) {
            return invocation.proceed();
        }

        // 全部数据权限，直接放行
        if (context.getScopeType() == null || context.getScopeType() == DataScopeType.ALL) {
            return invocation.proceed();
        }

        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        // 获取 @DataScope 注解
        DataScope dataScope = getDataScopeAnnotation(ms);
        if (dataScope == null) {
            return invocation.proceed();
        }

        // 构建数据范围过滤条件
        String scopeCondition = buildScopeCondition(dataScope, context);
        if (scopeCondition == null || scopeCondition.isEmpty()) {
            return invocation.proceed();
        }

        // 修改 SQL
        BoundSql boundSql = ms.getBoundSql(parameter);
        String originalSql = boundSql.getSql();
        String newSql = addScopeCondition(originalSql, scopeCondition);

        // 创建新的 MappedStatement
        MappedStatement newMs = copyMappedStatement(ms, new BoundSqlSqlSource(boundSql, newSql, ms.getConfiguration()));
        invocation.getArgs()[0] = newMs;

        return invocation.proceed();
    }

    /**
     * 获取 Mapper 方法上的 @DataScope 注解
     *
     * @param ms MappedStatement对象
     * @return DataScope注解对象，如果未找到则返回null
     */
    private DataScope getDataScopeAnnotation(MappedStatement ms) {
        try {
            String id = ms.getId();
            int lastDot = id.lastIndexOf('.');
            String className = id.substring(0, lastDot);
            String methodName = id.substring(lastDot + 1);

            Class<?> mapperClass = Class.forName(className);
            for (Method method : mapperClass.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.getAnnotation(DataScope.class);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 构建数据范围过滤条件
     *
     * @param dataScope 数据范围注解配置
     * @param context   当前数据范围上下文
     * @return 构建好的SQL过滤条件字符串
     */
    private String buildScopeCondition(DataScope dataScope, DataScopeContext context) {
        String tableAlias = dataScope.tableAlias();
        String prefix = tableAlias.isEmpty() ? "" : tableAlias + ".";
        String deptField = prefix + dataScope.deptField();
        String userField = prefix + dataScope.userField();

        DataScopeType scopeType = context.getScopeType();

        switch (scopeType) {
            case DEPT:
                Set<Long> deptIds = context.getDeptIds();
                if (deptIds == null || deptIds.isEmpty()) {
                    return "1 = 0";
                }
                return deptField + " IN (" + joinIds(deptIds) + ")";

            case DEPT_AND_CHILD:
                Set<Long> accessibleDeptIds = context.getAccessibleDeptIds();
                if (accessibleDeptIds == null || accessibleDeptIds.isEmpty()) {
                    return "1 = 0";
                }
                return deptField + " IN (" + joinIds(accessibleDeptIds) + ")";

            case SELF:
                Long userId = context.getUserId();
                if (userId == null) {
                    return "1 = 0";
                }
                return userField + " = " + userId;

            default:
                return null;
        }
    }

    /**
     * 将ID集合转换为逗号分隔的字符串
     *
     * @param ids ID集合
     * @return 逗号分隔的ID字符串
     */
    private String joinIds(Set<Long> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    /**
     * 在 SQL 中添加数据范围条件
     *
     * @param sql       原始SQL语句
     * @param condition 要添加的数据范围条件
     * @return 添加条件后的SQL语句
     */
    private String addScopeCondition(String sql, String condition) {
        String lowerSql = sql.toLowerCase();
        int whereIndex = lowerSql.lastIndexOf(" where ");

        if (whereIndex > 0) {
            int insertPos = whereIndex + 7;
            return sql.substring(0, insertPos) + "(" + condition + ") AND " + sql.substring(insertPos);
        } else {
            int insertPos = findInsertPosition(lowerSql);
            if (insertPos > 0) {
                return sql.substring(0, insertPos) + " WHERE " + condition + " " + sql.substring(insertPos);
            } else {
                return sql + " WHERE " + condition;
            }
        }
    }

    /**
     * 查找WHERE子句的插入位置
     *
     * @param lowerSql 转换为小写的SQL语句
     * @return WHERE子句应该插入的位置索引
     */
    private int findInsertPosition(String lowerSql) {
        String[] keywords = {" group by ", " order by ", " limit ", " having "};
        int minPos = -1;
        for (String keyword : keywords) {
            int pos = lowerSql.indexOf(keyword);
            if (pos > 0 && (minPos < 0 || pos < minPos)) {
                minPos = pos;
            }
        }
        return minPos;
    }

    /**
     * 复制MappedStatement并替换SQL源
     *
     * @param ms          原始MappedStatement对象
     * @param newSqlSource 新的SQL源对象
     * @return 复制后的新MappedStatement对象
     */
    private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(String.join(",", ms.getKeyProperties()));
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 自定义SQL源实现类，用于包装修改后的SQL语句
     */
    private static class BoundSqlSqlSource implements SqlSource {
        private final BoundSql originalBoundSql;
        private final String newSql;
        private final org.apache.ibatis.session.Configuration configuration;

        public BoundSqlSqlSource(BoundSql originalBoundSql, String newSql, org.apache.ibatis.session.Configuration configuration) {
            this.originalBoundSql = originalBoundSql;
            this.newSql = newSql;
            this.configuration = configuration;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return new BoundSql(configuration, newSql,
                    originalBoundSql.getParameterMappings(), parameterObject);
        }
    }
}
