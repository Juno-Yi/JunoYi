# JunoYi 数据范围（DataScope）技术文档

## 一、概述

数据范围（DataScope）是一种**行级数据权限控制**机制，用于控制用户能够查看哪些数据记录。它基于 MyBatis-Plus `DataPermissionHandler` 实现，在 SQL 执行前自动添加过滤条件，对业务代码**完全透明**。

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

### 1.3 两种使用模式

| 模式 | 配置 | 说明 |
|------|------|------|
| 注解模式（默认） | `global-enabled: false` | 仅对标注 `@DataScope` 的 Mapper 方法生效 |
| 全局模式 | `global-enabled: true` | 对所有查询生效，包括 MyBatis-Plus 内置方法（selectPage、selectList 等） |

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
│  │  DataPermissionInterceptor + DataScopeHandler            │    │
│  │  (MyBatis-Plus 数据权限插件)                              │    │
│  │  - 注解模式: 检查 @DataScope 注解                         │    │
│  │  - 全局模式: 对所有查询生效                               │    │
│  │  - 从 ThreadLocal 获取数据范围上下文                      │    │
│  │  - 使用 JSqlParser 修改 SQL 添加过滤条件                  │    │
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
  DataScopeHandler
       │
       ├─→ 检查 @DataScope 注解（注解模式）或直接处理（全局模式）
       │
       ├─→ 获取 DataScopeContextHolder 上下文
       │
       ├─→ 根据 scopeType 构建过滤条件:
       │       - ALL: 不添加条件
       │       - DEPT: dept_id IN (用户部门)
       │       - DEPT_AND_CHILD: dept_id IN (可访问部门)
       │       - SELF: create_by = userId
       │
       └─→ 使用 JSqlParser 修改 SQL，添加 WHERE 条件
```

---

## 三、核心文件位置

### 3.1 框架层 (junoyi-framework-datasource)

| 文件 | 说明 |
|------|------|
| `datascope/DataScopeType.java` | 数据范围类型枚举，定义 ALL/DEPT/DEPT_AND_CHILD/SELF |
| `datascope/DataScopeContextHolder.java` | ThreadLocal 上下文持有者，存储当前请求的数据范围信息 |
| `datascope/annotation/DataScope.java` | @DataScope 注解，标记需要数据范围过滤的 Mapper 方法 |
| `datascope/annotation/IgnoreDataScope.java` | @IgnoreDataScope 注解，标记忽略数据范围过滤的 Mapper 类或方法 |
| `datascope/handler/DataScopeHandler.java` | MyBatis-Plus DataPermissionHandler 实现，自动修改 SQL |
| `config/MyBatisPlusConfig.java` | 配置类，注册 DataPermissionInterceptor |
| `properties/DataSourceProperties.java` | 配置属性，包含数据范围开关和模式设置 |

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

## 四、配置说明

### 4.1 YAML 配置

```yaml
junoyi:
  datasource:
    data-scope:
      # 是否启用数据范围（默认 true）
      enabled: true
      # 是否启用全局模式（默认 false）
      # false: 仅对标注 @DataScope 的方法生效
      # true: 对所有查询生效，包括 MyBatis-Plus 内置方法
      global-enabled: false
      # 默认部门字段名（默认 dept_id）
      default-dept-field: dept_id
      # 默认用户字段名（默认 create_by）
      default-user-field: create_by
```

### 4.2 模式选择建议

| 场景 | 推荐模式 | 说明 |
|------|----------|------|
| 新项目 | 全局模式 | 所有查询自动过滤，安全性高 |
| 老项目迁移 | 注解模式 | 逐步添加注解，风险可控 |
| 混合场景 | 注解模式 | 精确控制哪些查询需要过滤 |

### 4.3 全局模式下的系统表排除

> **重要说明**：全局模式下，框架会**自动排除系统管理相关的表**，不对其应用数据范围过滤。

**排除的 Mapper 列表**：
- `SysUserMapper` - 用户管理
- `SysRoleMapper` - 角色管理
- `SysDeptMapper` - 部门管理
- `SysMenuMapper` - 菜单管理
- `SysPermissionMapper` - 权限管理
- `SysSessionMapper` - 会话管理
- `SysPermGroupMapper` - 权限组管理
- `SysUserRoleMapper` - 用户角色关联
- `SysUserDeptMapper` - 用户部门关联
- `SysUserGroupMapper` - 用户权限组关联
- `SysUserPermMapper` - 用户独立权限
- `SysRoleGroupMapper` - 角色权限组关联
- `SysDeptGroupMapper` - 部门权限组关联

**设计原因**：
- 系统管理功能（用户/角色/部门/菜单等）是**管理员专用**的功能
- 这些功能通过**菜单权限**控制访问，而非数据范围
- 管理员需要看到所有用户、所有角色才能正常管理系统
- 数据范围主要用于**业务数据**（如订单、客户、合同等）的行级权限控制

**如果你的业务 Mapper 也需要排除**，可以使用 `@IgnoreDataScope` 注解：
```java
@IgnoreDataScope
@Mapper
public interface YourSpecialMapper extends BaseMapper<YourEntity> {
    // 该 Mapper 的所有方法都不会应用数据范围
}
```

### 4.4 配置角色数据范围

在数据库中设置角色的 `data_scope` 字段：

```sql
-- 超级管理员：全部数据
UPDATE sys_role SET data_scope = '1' WHERE role_key = 'admin';

-- 部门经理：本部门及下级
UPDATE sys_role SET data_scope = '3' WHERE role_key = 'dept_manager';

-- 普通员工：仅本人
UPDATE sys_role SET data_scope = '4' WHERE role_key = 'employee';
```

---

## 五、基础使用

### 5.1 业务表设计

确保业务表包含以下字段（字段名可自定义）：

```sql
CREATE TABLE biz_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL COMMENT '订单号',
    amount DECIMAL(10,2) COMMENT '金额',
    -- 数据范围必需字段
    dept_id BIGINT COMMENT '所属部门ID',
    create_by VARCHAR(64) COMMENT '创建人用户名',
    -- 其他字段
    create_time DATETIME,
    update_time DATETIME
);

-- 建议添加索引
CREATE INDEX idx_order_dept ON biz_order(dept_id);
CREATE INDEX idx_order_creator ON biz_order(create_by);
```

> **注意**：`create_by` 字段存储的是**用户名（userName）**而非用户ID，这与系统的 BaseEntity 设计保持一致。SELF 模式会生成 `WHERE create_by = 'admin'` 这样的字符串比较条件。

### 5.2 注解模式使用

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

### 5.3 全局模式使用

启用全局模式后，MyBatis-Plus 内置方法也会自动应用数据范围：

```yaml
junoyi:
  datasource:
    data-scope:
      global-enabled: true
```

```java
@Service
public class BizOrderServiceImpl {

    @Autowired
    private BizOrderMapper orderMapper;

    public IPage<BizOrder> getOrderPage(Page<BizOrder> page, BizOrderQuery query) {
        // 直接使用 MyBatis-Plus 内置方法，自动应用数据范围
        LambdaQueryWrapper<BizOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getStatus() != null, BizOrder::getStatus, query.getStatus());
        return orderMapper.selectPage(page, wrapper);
    }

    public List<BizOrder> getOrderList() {
        // selectList 也会自动过滤
        return orderMapper.selectList(null);
    }
}
```

### 5.4 XML 中的 SQL

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

## 六、高级用法

### 6.1 自定义字段名

当业务表的字段名与默认值不同时：

```java
@DataScope(deptField = "department_id", userField = "creator_id")
List<BizOrder> selectOrderList(BizOrderQuery query);
```

### 6.2 多表关联查询

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

### 6.3 使用 @IgnoreDataScope 忽略数据范围

对于不需要数据范围过滤的 Mapper，可以使用 `@IgnoreDataScope` 注解：

```java
// 方式1：在 Mapper 类上标注，整个 Mapper 的所有方法都忽略数据范围
@IgnoreDataScope
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
    // 所有方法都不会应用数据范围过滤
}

// 方式2：在方法上标注，只忽略特定方法
@Mapper
public interface BizOrderMapper extends BaseMapper<BizOrder> {

    @DataScope
    List<BizOrder> selectOrderList(BizOrderQuery query);

    @IgnoreDataScope
    OrderStatVO selectOrderStat();  // 统计查询不过滤
}
```

> **注意**：全局模式下，系统表（`sys_*`）的 Mapper 会自动忽略数据范围，无需手动添加注解。

### 6.4 临时跳过数据范围

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

### 6.5 手动设置数据范围

在非 HTTP 请求场景（如定时任务、消息消费）中手动设置：

```java
@Component
public class OrderSyncTask {

    @Scheduled(cron = "0 0 2 * * ?")
    public void syncOrders() {
        // 以超级管理员身份执行
        DataScopeContextHolder.set(DataScopeContextHolder.DataScopeContext.builder()
                .userId(1L)
                .userName("admin")  // SELF 模式需要用户名
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

### 6.6 动态数据范围

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
                .userName(ctx.getUserName())  // 保留用户名
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

## 七、SQL 生成规则

### 7.1 不同数据范围的 SQL 示例

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
-- 用户名: admin（create_by 存储的是用户名，不是用户ID）
SELECT * FROM biz_order WHERE (create_by = 'admin') AND status = 1 ORDER BY create_time DESC
```

### 7.2 无数据时的处理

当用户没有任何部门时，生成 `1 = 0` 条件确保查不到数据：

```sql
-- 用户没有部门，DEPT 模式
SELECT * FROM biz_order WHERE (1 = 0) AND status = 1 ORDER BY create_time DESC
```

---

## 八、调试与排查

### 8.1 开启 DEBUG 日志

```yaml
logging:
  level:
    com.junoyi.framework.datasource: DEBUG
    com.junoyi.framework.security.filter: DEBUG
```

### 8.2 查看登录时的数据范围计算

登录时会输出：
```
[权限加载] 用户角色: [2, 3]
[权限加载] 用户部门: [5]
[权限加载] 数据范围: 3 (本部门及下级数据)
[权限加载] 可访问部门: [5, 10, 11, 12]
```

### 8.3 查看 Redis 中的会话数据

```bash
# 查看会话
redis-cli GET "junoyi:session:{tokenId}"
```

关键字段：
- `dataScope`: 数据范围类型 (1/2/3/4)
- `accessibleDeptIds`: 可访问的部门ID列表
- `depts`: 用户所属部门
- `superAdmin`: 是否超级管理员

### 8.4 常见问题排查

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 数据范围不生效 | 注解模式下 Mapper 方法没有 @DataScope 注解 | 添加注解或启用全局模式 |
| 查不到任何数据 | 用户没有部门或角色 | 检查用户关联数据 |
| 超级管理员也被过滤 | superAdmin 字段为 false | 检查 userId=1 或 permissions 包含 * |
| 修改角色后不生效 | 会话缓存未更新 | 用户重新登录 |
| SQL 语法错误 | 复杂 SQL 拦截器处理不当 | 使用手动方式处理 |

---

## 九、性能优化

### 9.1 索引优化

```sql
-- 必须索引
CREATE INDEX idx_table_dept ON your_table(dept_id);
CREATE INDEX idx_table_creator ON your_table(create_by);

-- 复合索引（如果经常按状态+部门查询）
CREATE INDEX idx_table_status_dept ON your_table(status, dept_id);
```

### 9.2 减少部门层级

部门层级过深会导致 `accessibleDeptIds` 集合过大，建议：
- 控制部门层级在 5 层以内
- 定期清理无效部门

### 9.3 缓存部门树

如果部门数据变化不频繁，可以缓存部门树结构：

```java
@Cacheable(value = "deptTree", key = "#parentId")
public Set<Long> getChildDeptIds(Long parentId) {
    // 递归查询子部门
}
```

---

## 十、最佳实践

1. **统一字段命名**：所有业务表使用相同的 `dept_id` 和 `create_by` 字段名
2. **默认添加注解**：所有列表查询方法默认添加 `@DataScope` 注解（注解模式）
3. **详情查询不过滤**：单条记录查询（如 getById）通常不需要数据范围过滤
4. **导出功能注意**：数据导出功能必须添加数据范围过滤
5. **定时任务特殊处理**：定时任务需要手动设置上下文或以管理员身份执行
6. **测试覆盖**：为不同数据范围编写单元测试

---

## 十一、相关文档

- [JunoYi 权限系统设计文档](./JunoYi权限系统设计文档.md)
- [JunoYi 项目架构说明](./项目架构说明.md)
