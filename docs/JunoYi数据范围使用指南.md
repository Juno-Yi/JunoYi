# JunoYi 数据范围（DataScope）技术文档

## 一、概述

数据范围（DataScope）是一种**行级数据权限控制**机制，用于控制用户能够查看哪些数据记录。它基于 MyBatis 拦截器实现，在 SQL 执行前自动添加过滤条件，对业务代码**完全透明**。

### 1.1 应用场景

- 部门经理只能查看本部门及下属部门的数据
- 普通员工只能查看自己创建的数据
- 区域负责人只能查看所辖区域的数据
- 超级管理员可以查看所有数据

### 1.2 数据范围类型

| 值 | 枚举 | 说明 | 优先级 |
|---|------|------|--------|
| 1 | ALL | 全部数据，不做任何过滤 | 最高 |
| 2 | DEPT | 仅本部门数据 | 第三 |
| 3 | DEPT_AND_CHILD | 本部门及所有下级部门数据 | 第二 |
| 4 | SELF | 仅本人创建的数据 | 最低 |

> **多角色取并集**：当用户拥有多个角色时，取权限最大的数据范围。例如用户同时拥有"部门经理"(DEPT_AND_CHILD) 和"普通员工"(SELF) 两个角色，最终数据范围为 DEPT_AND_CHILD。

---

## 二、架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         请求流程                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  HTTP Request                                                    │
│       │                                                          │
│       ▼                                                          │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  TokenAuthenticationTokenFilter (安全过滤器)              │    │
│  │  - 验证 Token                                            │    │
│  │  - 从 Redis 获取 UserSession                             │    │
│  │  - 设置 DataScopeContextHolder (ThreadLocal)             │    │
│  └─────────────────────────────────────────────────────────┘    │
│       │                                                          │
│       ▼                                                          │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  Controller → Service → Mapper                           │    │
│  │  (业务代码无需关心数据范围)                                │    │
│  └─────────────────────────────────────────────────────────┘    │
│       │                                                          │
│       ▼                                                          │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  DataScopeInterceptor (MyBatis 拦截器)                   │    │
│  │  - 检查方法是否有 @DataScope 注解                         │    │
│  │  - 从 ThreadLocal 获取数据范围上下文                      │    │
│  │  - 自动修改 SQL 添加过滤条件                              │    │
│  └─────────────────────────────────────────────────────────┘    │
│       │                                                          │
│       ▼                                                          │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  Database                                                │    │
│  │  执行带过滤条件的 SQL                                     │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 数据流转

```
登录时:
  SysAuthServiceImpl.login()
       │
       ├─→ 查询用户角色 → 获取每个角色的 dataScope
       │
       ├─→ DataScopeType.max() 取最大权限
       │
       ├─→ 根据 dataScope 计算 accessibleDeptIds
       │       - ALL: 不需要
       │       - DEPT: 用户所属部门
       │       - DEPT_AND_CHILD: 用户所属部门 + 递归查询所有下级部门
       │       - SELF: 不需要
       │
       └─→ 存入 UserSession → 缓存到 Redis

请求时:
  TokenAuthenticationTokenFilter
       │
       ├─→ 从 Redis 获取 UserSession
       │
       └─→ 设置 DataScopeContextHolder
               - userId
               - deptIds (用户所属部门)
               - scopeType (数据范围类型)
               - accessibleDeptIds (可访问的部门ID集合)
               - superAdmin (是否超级管理员)

SQL执行时:
  DataScopeInterceptor
       │
       ├─→ 检查 @DataScope 注解
       │
       ├─→ 获取 DataScopeContextHolder 上下文
       │
       ├─→ 根据 scopeType 构建过滤条件:
       │       - ALL: 不添加条件
       │       - DEPT: dept_id IN (用户部门)
       │       - DEPT_AND_CHILD: dept_id IN (可访问部门)
       │       - SELF: create_by = userId
       │
       └─→ 修改原始 SQL，添加 WHERE 条件
```

---

## 三、核心文件位置

### 3.1 框架层 (junoyi-framework-datasource)

| 文件 | 说明 |
|------|------|
| `datascope/DataScopeType.java` | 数据范围类型枚举，定义 ALL/DEPT/DEPT_AND_CHILD/SELF |
| `datascope/DataScopeContextHolder.java` | ThreadLocal 上下文持有者，存储当前请求的数据范围信息 |
| `datascope/annotation/DataScope.java` | @DataScope 注解，标记需要数据范围过滤的 Mapper 方法 |
| `datascope/interceptor/DataScopeInterceptor.java` | MyBatis 拦截器，自动修改 SQL 添加过滤条件 |
| `config/MyBatisPlusConfig.java` | 配置类，注册 DataScopeInterceptor |

### 3.2 安全层 (junoyi-framework-security)

| 文件 | 说明 |
|------|------|
| `module/LoginUser.java` | 登录用户信息，包含 dataScope 和 accessibleDeptIds |
| `module/UserSession.java` | 用户会话信息（存储在 Redis），包含数据范围字段 |
| `filter/TokenAuthenticationTokenFilter.java` | Token 过滤器，设置/清理 DataScopeContextHolder |
| `helper/SessionHelperImpl.java` | 会话管理，创建/更新会话时传递数据范围字段 |

### 3.3 业务层 (junoyi-module-system)

| 文件 | 说明 |
|------|------|
| `service/SysAuthServiceImpl.java` | 登录服务，计算用户数据范围和可访问部门 |

### 3.4 数据库表

| 表名 | 关键字段 | 说明 |
|------|----------|------|
| `sys_role` | `data_scope` | 角色的数据范围配置 (1/2/3/4) |
| `sys_dept` | `id`, `parent_id` | 部门表，树形结构 |
| `sys_user_dept` | `user_id`, `dept_id` | 用户-部门关联表（多对多） |
| `sys_user_role` | `user_id`, `role_id` | 用户-角色关联表 |

---

## 四、基础使用

### 4.1 配置角色数据范围

在数据库中设置角色的 `data_scope` 字段：

```sql
-- 超级管理员：全部数据
UPDATE sys_role SET data_scope = '1' WHERE role_key = 'admin';

-- 部门经理：本部门及下级
UPDATE sys_role SET data_scope = '3' WHERE role_key = 'dept_manager';

-- 普通员工：仅本人
UPDATE sys_role SET data_scope = '4' WHERE role_key = 'employee';
```

### 4.2 业务表设计

确保业务表包含以下字段（字段名可自定义）：

```sql
CREATE TABLE biz_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL COMMENT '订单号',
    amount DECIMAL(10,2) COMMENT '金额',
    -- 数据范围必需字段
    dept_id BIGINT COMMENT '所属部门ID',
    create_by BIGINT COMMENT '创建人ID',
    -- 其他字段
    create_time DATETIME,
    update_time DATETIME
);

-- 建议添加索引
CREATE INDEX idx_order_dept ON biz_order(dept_id);
CREATE INDEX idx_order_creator ON biz_order(create_by);
```

### 4.3 Mapper 添加注解

在需要数据范围过滤的 Mapper 方法上添加 `@DataScope` 注解：

```java
@Mapper
public interface BizOrderMapper extends BaseMapper<BizOrder> {

    /**
     * 查询订单列表（使用默认字段名 dept_id, create_by）
     */
    @DataScope
    List<BizOrder> selectOrderList(BizOrderQuery query);

    /**
     * 分页查询订单
     */
    @DataScope
    IPage<BizOrder> selectOrderPage(Page<BizOrder> page, @Param("query") BizOrderQuery query);
}
```

### 4.4 XML 中的 SQL

```xml
<select id="selectOrderList" resultType="BizOrder">
    SELECT * FROM biz_order
    WHERE del_flag = 0
    ORDER BY create_time DESC
</select>
```

拦截器会自动将其转换为：

```sql
-- 部门经理 (DEPT_AND_CHILD, accessibleDeptIds = [1, 2, 3])
SELECT * FROM biz_order
WHERE (dept_id IN (1, 2, 3)) AND del_flag = 0
ORDER BY create_time DESC

-- 普通员工 (SELF, userId = 100)
SELECT * FROM biz_order
WHERE (create_by = 100) AND del_flag = 0
ORDER BY create_time DESC
```

---

## 五、高级用法

### 5.1 自定义字段名

当业务表的字段名与默认值不同时：

```java
@DataScope(deptField = "department_id", userField = "creator_id")
List<BizOrder> selectOrderList(BizOrderQuery query);
```

### 5.2 多表关联查询

使用 `tableAlias` 指定表别名：

```java
@DataScope(tableAlias = "o")
List<BizOrderVO> selectOrderWithUser(@Param("query") BizOrderQuery query);
```

对应的 XML：

```xml
<select id="selectOrderWithUser" resultType="BizOrderVO">
    SELECT o.*, u.user_name, u.nick_name
    FROM biz_order o
    LEFT JOIN sys_user u ON o.create_by = u.user_id
    WHERE o.del_flag = 0
</select>
```

生成的 SQL：

```sql
SELECT o.*, u.user_name, u.nick_name
FROM biz_order o
LEFT JOIN sys_user u ON o.create_by = u.user_id
WHERE (o.dept_id IN (1, 2, 3)) AND o.del_flag = 0
```

### 5.3 复杂查询场景

对于子查询或复杂 SQL，建议在 XML 中手动处理：

```xml
<select id="selectComplexOrder" resultType="BizOrderVO">
    SELECT * FROM (
        SELECT o.*, 
               (SELECT COUNT(*) FROM biz_order_item WHERE order_id = o.id) as item_count
        FROM biz_order o
        WHERE o.del_flag = 0
        <if test="@com.junoyi.framework.datasource.datascope.DataScopeContextHolder@get() != null">
            <choose>
                <when test="@com.junoyi.framework.datasource.datascope.DataScopeContextHolder@get().superAdmin">
                    <!-- 超级管理员不过滤 -->
                </when>
                <when test="@com.junoyi.framework.datasource.datascope.DataScopeContextHolder@get().scopeType.name() == 'SELF'">
                    AND o.create_by = #{@com.junoyi.framework.datasource.datascope.DataScopeContextHolder@get().userId}
                </when>
                <otherwise>
                    AND o.dept_id IN
                    <foreach collection="@com.junoyi.framework.datasource.datascope.DataScopeContextHolder@get().accessibleDeptIds" 
                             item="deptId" open="(" separator="," close=")">
                        #{deptId}
                    </foreach>
                </otherwise>
            </choose>
        </if>
    ) t
    ORDER BY t.create_time DESC
</select>
```

### 5.4 临时跳过数据范围

某些场景需要临时跳过数据范围过滤（如统计报表）：

```java
@Service
public class ReportServiceImpl {

    /**
     * 统计所有订单（跳过数据范围）
     */
    public OrderStatVO getOrderStat() {
        // 临时清除上下文
        DataScopeContext originalContext = DataScopeContextHolder.get();
        try {
            DataScopeContextHolder.clear();
            // 执行不带数据范围的查询
            return orderMapper.selectOrderStat();
        } finally {
            // 恢复上下文
            if (originalContext != null) {
                DataScopeContextHolder.set(originalContext);
            }
        }
    }
}
```

### 5.5 手动设置数据范围

在非 HTTP 请求场景（如定时任务、消息消费）中手动设置：

```java
@Component
public class OrderSyncTask {

    @Scheduled(cron = "0 0 2 * * ?")
    public void syncOrders() {
        // 以超级管理员身份执行
        DataScopeContextHolder.set(DataScopeContextHolder.DataScopeContext.builder()
                .userId(1L)
                .superAdmin(true)
                .build());
        
        try {
            // 执行业务逻辑
            orderService.syncAllOrders();
        } finally {
            DataScopeContextHolder.clear();
        }
    }
}
```

### 5.6 动态数据范围

某些场景需要根据业务动态调整数据范围：

```java
@Service
public class OrderServiceImpl {

    /**
     * 查看指定部门的订单（临时扩大数据范围）
     */
    public List<BizOrder> getOrdersByDept(Long targetDeptId) {
        DataScopeContext ctx = DataScopeContextHolder.get();
        
        // 检查用户是否有权限查看目标部门
        if (ctx != null && !ctx.isSuperAdmin()) {
            if (ctx.getAccessibleDeptIds() == null || 
                !ctx.getAccessibleDeptIds().contains(targetDeptId)) {
                throw new NoPermissionException("无权查看该部门数据");
            }
        }
        
        // 临时设置只查看指定部门
        DataScopeContextHolder.set(DataScopeContextHolder.DataScopeContext.builder()
                .userId(ctx.getUserId())
                .deptIds(Set.of(targetDeptId))
                .scopeType(DataScopeType.DEPT)
                .accessibleDeptIds(Set.of(targetDeptId))
                .superAdmin(false)
                .build());
        
        try {
            return orderMapper.selectOrderList(null);
        } finally {
            // 恢复原始上下文
            DataScopeContextHolder.set(ctx);
        }
    }
}
```

---

## 六、SQL 生成规则

### 6.1 不同数据范围的 SQL 示例

原始 SQL：
```sql
SELECT * FROM biz_order WHERE status = 1 ORDER BY create_time DESC
```

**ALL (全部数据)**
```sql
-- 不修改，原样执行
SELECT * FROM biz_order WHERE status = 1 ORDER BY create_time DESC
```

**DEPT (本部门)**
```sql
-- 用户部门: [5]
SELECT * FROM biz_order WHERE (dept_id IN (5)) AND status = 1 ORDER BY create_time DESC
```

**DEPT_AND_CHILD (本部门及下级)**
```sql
-- 可访问部门: [5, 10, 11, 12]
SELECT * FROM biz_order WHERE (dept_id IN (5, 10, 11, 12)) AND status = 1 ORDER BY create_time DESC
```

**SELF (仅本人)**
```sql
-- 用户ID: 100
SELECT * FROM biz_order WHERE (create_by = 100) AND status = 1 ORDER BY create_time DESC
```

### 6.2 无数据时的处理

当用户没有任何部门时，生成 `1 = 0` 条件确保查不到数据：

```sql
-- 用户没有部门，DEPT 模式
SELECT * FROM biz_order WHERE (1 = 0) AND status = 1 ORDER BY create_time DESC
```

---

## 七、调试与排查

### 7.1 开启 DEBUG 日志

```yaml
logging:
  level:
    com.junoyi.framework.datasource: DEBUG
    com.junoyi.framework.security.filter: DEBUG
```

### 7.2 查看登录时的数据范围计算

登录时会输出：
```
[权限加载] 用户角色: [2, 3]
[权限加载] 用户部门: [5]
[权限加载] 数据范围: 3 (本部门及下级数据)
[权限加载] 可访问部门: [5, 10, 11, 12]
```

### 7.3 查看 Redis 中的会话数据

```bash
# 查看会话
redis-cli GET "junoyi:session:{tokenId}"
```

关键字段：
- `dataScope`: 数据范围类型 (1/2/3/4)
- `accessibleDeptIds`: 可访问的部门ID列表
- `depts`: 用户所属部门
- `superAdmin`: 是否超级管理员

### 7.4 常见问题排查

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 数据范围不生效 | Mapper 方法没有 @DataScope 注解 | 添加注解 |
| 查不到任何数据 | 用户没有部门或角色 | 检查用户关联数据 |
| 超级管理员也被过滤 | superAdmin 字段为 false | 检查 userId=1 或 permissions 包含 * |
| 修改角色后不生效 | 会话缓存未更新 | 用户重新登录 |
| SQL 语法错误 | 复杂 SQL 拦截器处理不当 | 使用手动方式处理 |

---

## 八、性能优化

### 8.1 索引优化

```sql
-- 必须索引
CREATE INDEX idx_table_dept ON your_table(dept_id);
CREATE INDEX idx_table_creator ON your_table(create_by);

-- 复合索引（如果经常按状态+部门查询）
CREATE INDEX idx_table_status_dept ON your_table(status, dept_id);
```

### 8.2 减少部门层级

部门层级过深会导致 `accessibleDeptIds` 集合过大，建议：
- 控制部门层级在 5 层以内
- 定期清理无效部门

### 8.3 缓存部门树

如果部门数据变化不频繁，可以缓存部门树结构：

```java
@Cacheable(value = "deptTree", key = "#parentId")
public Set<Long> getChildDeptIds(Long parentId) {
    // 递归查询子部门
}
```

---

## 九、扩展开发

### 9.1 添加新的数据范围类型

1. 在 `DataScopeType` 枚举中添加新类型：

```java
public enum DataScopeType {
    // ... 现有类型
    
    /**
     * 自定义部门（指定部门列表）
     */
    CUSTOM_DEPT("5", "自定义部门", 5);
}
```

2. 在 `DataScopeInterceptor.buildScopeCondition()` 中添加处理逻辑：

```java
case CUSTOM_DEPT:
    Set<Long> customDeptIds = context.getCustomDeptIds();
    if (customDeptIds == null || customDeptIds.isEmpty()) {
        return "1 = 0";
    }
    return deptField + " IN (" + joinIds(customDeptIds) + ")";
```

3. 在 `SysAuthServiceImpl.calculateDataScope()` 中添加计算逻辑。

### 9.2 支持多租户

结合多租户场景，可以在 `DataScopeContext` 中添加 `tenantId`：

```java
@Data
@Builder
public static class DataScopeContext {
    // ... 现有字段
    
    /**
     * 租户ID
     */
    private Long tenantId;
}
```

在拦截器中添加租户过滤：

```java
if (context.getTenantId() != null) {
    conditions.add("tenant_id = " + context.getTenantId());
}
```

---

## 十、最佳实践

1. **统一字段命名**：所有业务表使用相同的 `dept_id` 和 `create_by` 字段名
2. **默认添加注解**：所有列表查询方法默认添加 `@DataScope` 注解
3. **详情查询不过滤**：单条记录查询（如 getById）通常不需要数据范围过滤
4. **导出功能注意**：数据导出功能必须添加数据范围过滤
5. **定时任务特殊处理**：定时任务需要手动设置上下文或以管理员身份执行
6. **测试覆盖**：为不同数据范围编写单元测试

---

## 十一、相关文档

- [JunoYi 权限系统设计文档](./JunoYi权限系统设计文档.md)
- [JunoYi 项目架构说明](./项目架构说明.md)
