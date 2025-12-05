package com.junoyi.framework.core.utils.sql;

import com.junoyi.framework.core.utils.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * sql 操作工具类
 *
 * @author Fan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlUtils {

    /**
     * 定义常用的 sql 关键字
     */
    public static final String SQL_REGEX = "select |insert |delete |update |drop |count |exec |chr |mid |master |truncate |char |and |declare ";

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    public static final String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";


    /**
     * 检查字符串，防止注入绕过
     */
    public static String escapeOrderBySQL(String value){
        if (StringUtils.isNotEmpty(value)
                && !isValidOrderBySQL(value))
            // TODO: 异常后续需要修改成 UtilException 工具异常
            throw new RuntimeException("参数不符合规范，不能进行查询");

        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySQL(String value){
        return value.matches(SQL_PATTERN);
    }

    /**
     * SQL关键字检查
     */
    public static void filterKeyword(String value){
        if (StringUtils.isEmpty(value))
            return;
        String[] sqlKeywords = StringUtils.split(SQL_REGEX, "\\|");
        for (String sqlKeyword : sqlKeywords){
            if (StringUtils.indexOfIgnoreCase(value,sqlKeyword) > -1)
                // TODO: 异常后续需要修改成 UtilException 工具异常
                throw new RuntimeException("参数存在SQL注入风险");
        }
    }
}