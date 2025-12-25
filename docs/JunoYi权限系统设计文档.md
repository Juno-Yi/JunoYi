# JunoYi 权限系统设计文档

## 一、设计理念

### 1.1 核心思想

打破传统 RBAC 框架（RuoYi/Sa-Token/Shiro）的局限性，采用 **权限节点 + 权限组** 的极简设计模式，灵感来源于 Minecraft 服务器的 LuckPerms 权限插件。

**核心原则：权限节点就是字符串，不需要预先注册到数据库！**

### 1.2 与传统框架对比

| 问题 | 传统框架 | JunoYi 方案 |
|------|----------|-------------|
| 权限与菜单耦合 | 权限必须绑定菜单 | 权限节点独立，与菜单解耦 |
| 权限注册 | 必须预先在数据库注册 | **无需注册，直接使用字符串** |
| API 权限 | 每个 API 必须对应一个菜单 | 权限节点可独立定义 |
| 通配符支持 | 不支持 | 支持 `system.user.*` |
| 黑名单 | 不支持 | 支持 `-system.user.delete` |
| 数据范围 | 硬编码或简单配置 | 动态策略引擎 |

---

## 二、核心概念

### 2.1 权限节点（Permission Node）

权限节点是权限系统的最小单元，**就是一个字符串**，采用点分隔的层级结构：

```
system.user.create      # 创建用户
system.user.delete      # 删除用户
system.user.*           # 用户模块所有权限（通配符）
system.*                # 系统模块所有权限
*                       # 超级管理员（所有权限）
-system.user.delete     # 黑名单：禁止删除用户
```

### 2.2 权限组（Permission Group）

权限组直接存储权限字符串数组（JSON格式），**不需要关联权限表**：

```json
{
  "group_code": "user_manager",
  "group_name": "用户管理员",
  "permissions": [
    "system.user.*",
    "system.role.view",
    "-system.user.delete"
  ]
}
```

### 2.3 授权模型

```
用户 → 权限组 → 权限字符串数组
```

权限组可以关联：
- 用户（User）
- 角色（Role）
- 部门（Department）

---

## 三、架构设计

### 3.1 模块结构

```
junoyi-framework-permission/
├── annotation/
│   ├── Permission.java          # 权限校验注解
│   ├── DataScope.java           # 数据范围注解
│   └── FieldPermission.java     # 字段权限注解
├── aspect/
│   └── PermissionAspect.java    # 权限校验切面
├── enums/
│   ├── Logical.java             # 逻辑运算符（AND/OR）
│   ├── DataScopeType.java       # 数据范围类型
│   ├── PermissionType.java      # 权限类型
│   └── PermissionEffect.java    # 权限效果（ALLOW/DENY）
├── matcher/
│   └── PermissionMatcher.java   # 权限匹配器（支持通配符）
├── helper/
│   └── PermissionHelper.java    # 权限工具类
├── handler/
│   └── PermissionLoader.java    # 权限加载器接口
├── exception/
│   └── PermissionException.java # 权限异常
├── config/
│   └── PermissionConfiguration.java
└── properties/
    └── PermissionProperties.java
```

### 3.2 与 Security 模块集成

权限信息存储在 `SecurityContext` 的 `LoginUser` 中，统一管理认证和授权：

```
┌─────────────────────────────────────────────────────────┐
│                    SecurityContext                       │
│  ┌─────────────────────────────────────────────────┐    │
│  │                   LoginUser                      │    │
│  │  - userId, userName, nickName                   │    │
│  │  - permissions (Set<String>)  ← 权限节点        │    │
│  │  - groups (Set<String>)       ← 权限组          │    │
│  │  - deptId                                        │    │
│  │  - superAdmin                                    │    │
│  │  - roles                                         │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                          ↑
                          │ 读取
          ┌───────────────┴───────────────┐
          │                               │
    SecurityUtils              PermissionHelper
    (认证相关)                  (授权相关)
```

---

## 四、权限匹配规则

### 4.1 匹配优先级

```
黑名单（-开头） > 精确匹配 > 通配符匹配
```

### 4.2 通配符规则

| 模式 | 说明 | 示例 |
|------|------|------|
| `*` | 单级通配 | `system.user.*` 匹配 `system.user.delete` |
| `**` | 多级通配 | `system.**` 匹配 `system.user.delete.field` |
| `-` | 黑名单前缀 | `-system.user.delete` 禁止删除用户 |

### 4.3 匹配示例

```java
用户权限: ["system.user.*", "system.role.view", "-system.user.delete"]

system.user.create  → ✅ 匹配 system.user.*
system.user.delete  → ❌ 被黑名单禁止
system.user.view    → ✅ 匹配 system.user.*
system.role.view    → ✅ 精确匹配
system.role.edit    → ❌ 无匹配
```

---

## 五、使用指南

### 5.1 注解方式

```java
// 单个权限
@Permission("system.user.delete")
@DeleteMapping("/{id}")
public R<?> deleteUser(@PathVariable Long id) {
    // ...
}

// 多个权限（OR 关系，满足任意一个即可）
@Permission(value = {"system.user.view", "system.admin"}, logical = Logical.OR)
@GetMapping("/{id}")
public R<?> getUser(@PathVariable Long id) {
    // ...
}

// 多个权限（AND 关系，必须同时满足）
@Permission(value = {"system.user.view", "system.user.edit"}, logical = Logical.AND)
@PutMapping("/{id}")
public R<?> updateUser(@PathVariable Long id) {
    // ...
}

// 指定权限类型
@Permission(value = "button.user.export", type = PermissionType.UI_BUTTON)
@GetMapping("/export")
public void exportUsers() {
    // ...
}

// 自定义错误消息
@Permission(value = "system.user.delete", message = "您没有删除用户的权限")
@DeleteMapping("/{id}")
public R<?> deleteUser(@PathVariable Long id) {
    // ...
}
```

### 5.2 编程方式

```java
import com.junoyi.framework.permission.helper.PermissionHelper;

// 判断是否有权限
if (PermissionHelper.hasPermission("system.user.delete")) {
    // 执行删除操作
}

// 判断是否有任意一个权限
if (PermissionHelper.hasAnyPermission("system.user.view", "system.admin")) {
    // 执行查看操作
}

// 判断是否有所有权限
if (PermissionHelper.hasAllPermissions("system.user.view", "system.user.edit")) {
    // 执行编辑操作
}

// 判断是否为超级管理员
if (PermissionHelper.isSuperAdmin()) {
    // 超级管理员逻辑
}

// 判断是否在某个权限组
if (PermissionHelper.inGroup("admin")) {
    // 管理员组逻辑
}

// 获取当前用户权限列表
Set<String> permissions = PermissionHelper.getCurrentUserPermissions();

// 获取当前用户权限组列表
Set<String> groups = PermissionHelper.getCurrentUserGroups();
```

### 5.3 数据范围注解

```java
// 基于部门的数据范围
@DataScope(scopeType = DataScopeType.DEPT, scopeField = "dept_id")
public List<User> listUsers(UserQuery query) {
    // SQL 自动追加: AND dept_id = #{currentDeptId}
}

// 本部门及子部门
@DataScope(scopeType = DataScopeType.DEPT_AND_CHILD, scopeField = "dept_id")
public List<User> listUsers(UserQuery query) {
    // SQL 自动追加: AND dept_id IN (...)
}

// 仅本人数据
@DataScope(scopeType = DataScopeType.SELF, scopeField = "create_by")
public List<Order> listOrders(OrderQuery query) {
    // SQL 自动追加: AND create_by = #{currentUserId}
}

// 自定义 SQL
@DataScope(customSql = "region_id IN (SELECT region_id FROM user_region WHERE user_id = #{userId})")
public List<Report> listReports(ReportQuery query) {
    // ...
}
```

### 5.4 字段权限注解

```java
public class User {
    private Long id;
    private String username;

    // 薪资字段需要特定权限才能查看和编辑
    @FieldPermission(read = "field.user.salary.read", write = "field.user.salary.write")
    private BigDecimal salary;

    // 手机号需要脱敏显示
    @FieldPermission(read = "field.user.phone.read", mask = true, maskPattern = "PHONE")
    private String phone;
}
```

---

## 六、数据库设计

### 6.1 极简表结构（仅3张表）

#### sys_perm_group（权限组表）

```sql
CREATE TABLE sys_perm_group (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_code      VARCHAR(50) NOT NULL UNIQUE COMMENT '权限组编码',
    group_name      VARCHAR(100) NOT NULL COMMENT '权限组名称',
    permissions     TEXT COMMENT '权限字符串数组（JSON）',
    parent_id       BIGINT DEFAULT 0 COMMENT '父权限组（支持继承）',
    priority        INT DEFAULT 0 COMMENT '优先级',
    description     VARCHAR(500),
    status          TINYINT DEFAULT 1,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '权限组表';

-- 示例数据
INSERT INTO sys_perm_group (group_code, group_name, permissions, priority) VALUES
('super_admin', '超级管理员', '["*"]', 100),
('user_manager', '用户管理员', '["system.user.*", "system.role.view", "-system.user.delete"]', 50),
('guest', '访客', '["system.user.view", "system.dashboard.view"]', 0);
```

#### sys_user_group（用户-权限组关联表）

```sql
CREATE TABLE sys_user_group (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    group_id        BIGINT NOT NULL COMMENT '权限组ID',
    expire_time     DATETIME COMMENT '过期时间（支持临时授权）',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_group (user_id, group_id)
) COMMENT '用户-权限组关联表';
```

#### sys_role_group（角色-权限组关联表，可选）

```sql
CREATE TABLE sys_role_group (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id         BIGINT NOT NULL COMMENT '角色ID',
    group_id        BIGINT NOT NULL COMMENT '权限组ID',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_group (role_id, group_id)
) COMMENT '角色-权限组关联表';
```

---

## 七、配置项

```yaml
junoyi:
  permission:
    enable: true                    # 是否启用权限控制
    cache:
      enable: true                  # 是否启用缓存
      expire: 3600                  # 缓存过期时间（秒）
    super-admin:
      enable: true                  # 是否启用超级管理员
      user-ids: [1]                 # 超级管理员用户ID
      permission: "*"               # 超级管理员权限节点
    default-groups:
      - guest                       # 新用户默认权限组
```

---

## 八、前端集成

### 8.1 获取用户权限

登录成功后，前端可以获取用户的权限列表：

```javascript
// 登录响应中包含权限信息
{
  "accessToken": "xxx",
  "refreshToken": "xxx",
  "permissions": ["system.user.*", "system.role.view", "-system.user.delete"],
  "groups": ["user_manager"]
}
```

### 8.2 前端权限判断

```javascript
// 权限匹配工具函数
function hasPermission(permission) {
    const permissions = store.getters.permissions;
    
    // 1. 检查黑名单
    if (permissions.includes('-' + permission)) {
        return false;
    }
    
    // 2. 超级管理员
    if (permissions.includes('*')) {
        return true;
    }
    
    // 3. 精确匹配
    if (permissions.includes(permission)) {
        return true;
    }
    
    // 4. 通配符匹配
    return permissions.some(p => matchWildcard(p, permission));
}

function matchWildcard(pattern, permission) {
    if (pattern === '*' || pattern === '**') return true;
    if (pattern.endsWith('.*')) {
        const prefix = pattern.slice(0, -2);
        return permission.startsWith(prefix + '.');
    }
    if (pattern.endsWith('.**')) {
        const prefix = pattern.slice(0, -3);
        return permission.startsWith(prefix + '.');
    }
    return false;
}
```

### 8.3 Vue 指令

```vue
<template>
  <!-- 按钮权限 -->
  <el-button v-permission="'system.user.delete'" @click="handleDelete">删除</el-button>
  
  <!-- 权限组判断 -->
  <AdminPanel v-if="inGroup('admin')" />
  
  <!-- 字段权限 -->
  <el-table-column v-if="hasPermission('field.user.salary.read')" prop="salary" label="薪资" />
</template>

<script setup>
import { hasPermission, inGroup } from '@/utils/permission'
</script>
```

---

## 九、权限计算流程

```
用户登录
    ↓
查询用户关联的权限组
    ↓
合并所有权限组的 permissions 字段
    ↓
处理权限继承（父权限组）
    ↓
存入 LoginUser.permissions
    ↓
存入 Redis（UserSession）
    ↓
请求时从 SecurityContext 获取
    ↓
PermissionMatcher 进行字符串匹配
```

---

## 十、总结

JunoYi 权限系统的核心优势：

1. **极简设计** - 仅3张表，无需权限注册表
2. **权限即字符串** - 直接在代码中使用，无需预先配置
3. **通配符支持** - `system.user.*` 一次授权多个操作
4. **黑名单支持** - `-system.user.delete` 精确禁止
5. **高性能** - Redis 缓存 + 纯字符串匹配
6. **动态权限** - 修改即生效，无需重启
7. **多维度控制** - API/菜单/按钮/组件/行/字段 全覆盖
