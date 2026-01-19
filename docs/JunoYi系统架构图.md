# JunoYi 后端系统架构图

## 一、整体架构图（单体架构）

```mermaid
graph TB
    subgraph "客户端层 Client Layer"
        A1[Web前台]
        A2[Web后台]
        A3[移动端APP]
        A4[小程序]
        A5[桌面端]
    end
    
    subgraph "应用层 Application Layer - junoyi-server"
        B1[Spring Boot 内嵌 Tomcat<br/>端口: 7588]
    end
    
    subgraph "过滤器链 Filter Chain"
        C1[CorsFilter<br/>跨域处理]
        C2[ApiEncryptFilter<br/>API加密解密]
        C3[XssFilter<br/>XSS防护]
        C4[SqlInjectionFilter<br/>SQL注入防护]
        C5[TokenAuthenticationFilter<br/>Token认证]
    end
    
    subgraph "业务模块层 Business Module Layer"
        D1[junoyi-module-system<br/>系统管理模块]
        D2[junoyi-module-generation<br/>代码生成模块]
        D3[junoyi-module-demo<br/>示例模块]
        D4[junoyi-module-xxx<br/>扩展模块...]
    end
    
    subgraph "API定义层 API Definition Layer"
        E1[system-api<br/>接口/实体/Mapper]
        E2[generation-api<br/>接口/实体/Mapper]
        E3[demo-api<br/>接口/实体/Mapper]
    end

    subgraph "框架层 Framework Layer"
        F1[framework-web<br/>Web基础设施]
        F2[framework-security<br/>安全认证]
        F3[framework-permission<br/>权限控制]
        F4[framework-datasource<br/>数据源管理]
        F5[framework-redis<br/>缓存管理]
        F6[framework-captcha<br/>验证码]
        F7[framework-log<br/>日志框架]
        F8[framework-excel<br/>Excel处理]
        F9[framework-api-doc<br/>API文档]
        F10[framework-json<br/>JSON处理]
        F11[framework-event<br/>事件总线]
        F12[framework-quartz<br/>定时任务]
        F13[framework-core<br/>核心工具]
    end
    
    subgraph "依赖管理层 Dependency Layer"
        G1[junoyi-dependencies<br/>统一版本管理]
    end
    
    subgraph "基础设施层 Infrastructure Layer"
        H1[(MySQL 8.0+<br/>主数据库)]
        H2[(Redis 7.0+<br/>缓存数据库)]
        H3[本地文件存储<br/>日志/上传文件]
    end
    
    A1 & A2 & A3 & A4 & A5 --> B1
    B1 --> C1 --> C2 --> C3 --> C4 --> C5
    C5 --> D1 & D2 & D3 & D4
    D1 --> E1
    D2 --> E2
    D3 --> E3
    E1 & E2 & E3 --> F1 & F2 & F3 & F4 & F5 & F6 & F7 & F8 & F9 & F10 & F11 & F12 & F13
    F1 & F2 & F3 & F4 & F5 & F6 & F7 & F8 & F9 & F10 & F11 & F12 & F13 --> G1
    F4 --> H1
    F5 --> H2
    B1 --> H3
```

---

## 二、请求处理流程图

```mermaid
sequenceDiagram
    participant Client as 客户端
    participant Filter as 过滤器链
    participant Interceptor as 拦截器
    participant Controller as 控制器
    participant Service as 服务层
    participant Mapper as 数据访问层
    participant DB as 数据库
    participant Redis as 缓存
    
    Client->>Filter: 1. HTTP请求
    Filter->>Filter: 2. CORS跨域处理
    Filter->>Filter: 3. API加密解密
    Filter->>Filter: 4. XSS防护
    Filter->>Filter: 5. SQL注入防护
    Filter->>Interceptor: 6. Token认证
    Interceptor->>Interceptor: 7. 权限校验
    Interceptor->>Interceptor: 8. 访问日志记录
    Interceptor->>Controller: 9. 路由到控制器
    Controller->>Controller: 10. 参数校验
    Controller->>Service: 11. 调用业务逻辑
    Service->>Redis: 12. 查询缓存
    alt 缓存命中
        Redis-->>Service: 返回缓存数据
    else 缓存未命中
        Service->>Mapper: 13. 查询数据库
        Mapper->>DB: 14. 执行SQL
        DB-->>Mapper: 15. 返回结果
        Mapper-->>Service: 16. 返回数据
        Service->>Redis: 17. 更新缓存
    end
    Service-->>Controller: 18. 返回业务结果
    Controller-->>Client: 19. 统一响应格式
```

---

## 三、模块依赖关系图

```mermaid
graph TB
    subgraph "启动模块"
        A[junoyi-server<br/>启动入口]
    end
    
    subgraph "业务模块实现层"
        B1[junoyi-module-system<br/>系统模块实现]
        B2[junoyi-module-generation<br/>代码生成实现]
        B3[junoyi-module-demo<br/>示例模块实现]
    end
    
    subgraph "业务模块API层"
        C1[junoyi-module-system-api<br/>系统模块API]
        C2[junoyi-module-generation-api<br/>代码生成API]
        C3[junoyi-module-demo-api<br/>示例模块API]
    end
    
    subgraph "框架启动器"
        D0[junoyi-framework-boot-starter<br/>框架启动核心<br/>聚合所有框架模块]
    end
    
    subgraph "框架模块层"
        D1[framework-web<br/>Web基础]
        D2[framework-security<br/>安全认证]
        D3[framework-permission<br/>权限控制]
        D4[framework-datasource<br/>数据源]
        D5[framework-redis<br/>缓存]
        D6[framework-captcha<br/>验证码]
        D7[framework-json<br/>JSON]
        D8[framework-excel<br/>Excel]
        D9[framework-event<br/>事件]
        D10[framework-api-doc<br/>API文档]
        D11[framework-core<br/>核心工具]
    end
    
    subgraph "依赖管理"
        E[junoyi-dependencies<br/>统一版本管理]
    end
    
    A --> B1 & B2 & B3
    B1 --> C1
    B2 --> C2
    B3 --> C3
    C1 & C2 & C3 --> D0
    D0 --> D1 & D2 & D3 & D4 & D5 & D6 & D7 & D8 & D9 & D10
    D1 & D2 & D3 & D4 & D5 & D6 & D7 & D8 & D9 & D10 --> D11
    D11 --> E
    
    style D0 fill:#f9f,stroke:#333,stroke-width:3px
```

**依赖说明**：
1. **junoyi-server** 只依赖业务模块实现（system、demo 等）
2. **业务模块实现** 依赖对应的 **API 模块**
3. **API 模块** 统一依赖 **framework-boot-starter**
4. **boot-starter** 聚合了所有框架模块，简化依赖管理
5. 所有框架模块最终依赖 **framework-core** 核心模块
6. **dependencies** 统一管理所有模块的版本号

---

## 四、框架层详细架构

```mermaid
graph TB
    subgraph "junoyi-framework-web Web基础设施"
        W1[统一异常处理<br/>GlobalExceptionHandler]
        W2[跨域配置<br/>CorsConfiguration]
        W3[XSS防护<br/>XssFilter]
        W4[SQL注入防护<br/>SqlInjectionFilter]
        W5[访问日志<br/>AccessLogInterceptor]
        W6[统一响应封装<br/>Result]
    end
    
    subgraph "junoyi-framework-security 安全认证"
        S1[JWT Token生成<br/>JwtTokenHelper]
        S2[Token认证过滤器<br/>TokenAuthenticationFilter]
        S3[RSA加密解密<br/>RsaCryptoHelper]
        S4[API加密过滤器<br/>ApiEncryptFilter]
        S5[密码工具<br/>PasswordUtils]
        S6[会话管理<br/>SessionHelper]
    end

    subgraph "junoyi-framework-permission 权限控制"
        P1[权限注解<br/>@Permission]
        P2[权限切面<br/>PermissionAspect]
        P3[权限匹配器<br/>PermissionMatcher]
        P4[字段权限<br/>@FieldPermission]
        P5[字段脱敏<br/>MaskUtils]
        P6[权限助手<br/>PermissionHelper]
    end
    
    subgraph "junoyi-framework-datasource 数据源管理"
        DS1[MyBatis-Plus配置<br/>MyBatisPlusConfig]
        DS2[分页插件<br/>PaginationInterceptor]
        DS3[数据权限<br/>DataScopeHandler]
        DS4[慢SQL监控<br/>SlowSqlInterceptor]
        DS5[SQL美化<br/>SqlBeautifyInterceptor]
        DS6[动态数据源<br/>DynamicDataSource]
    end
    
    subgraph "junoyi-framework-redis 缓存管理"
        R1[Redis工具类<br/>RedisUtils]
        R2[Redisson配置<br/>RedissonConfig]
        R3[分布式锁<br/>Lock4j]
        R4[缓存注解<br/>@Cacheable]
    end
    
    subgraph "junoyi-framework-captcha 验证码"
        CA1[图形验证码<br/>ImageCaptcha]
        CA2[滑块验证码<br/>SlideCaptcha]
        CA3[验证码存储<br/>CaptchaStore]
        CA4[验证码助手<br/>CaptchaHelper]
    end
    
    subgraph "junoyi-framework-log 日志框架"
        L1[日志配置<br/>JunoYiLoggingConfig]
        L2[日志工具<br/>JunoYiLog]
        L3[操作日志<br/>@OperationLog]
        L4[日志切面<br/>LogAspect]
    end
    
    subgraph "junoyi-framework-core 核心工具"
        C1[统一返回<br/>Result]
        C2[分页对象<br/>PageQuery/PageResult]
        C3[基础异常<br/>BaseException]
        C4[工具类<br/>Utils]
        C5[常量定义<br/>Constants]
        C6[对象转换<br/>MapStruct]
    end
```

---

## 五、系统模块详细架构

```mermaid
graph TB
    subgraph "系统管理模块 junoyi-module-system"
        SYS1[用户管理<br/>SysUserController]
        SYS2[角色管理<br/>SysRoleController]
        SYS3[菜单管理<br/>SysMenuController]
        SYS4[部门管理<br/>SysDeptController]
        SYS5[权限管理<br/>SysPermissionController]
        SYS6[会话管理<br/>SysSessionController]
        SYS7[缓存管理<br/>SysCacheController]
        SYS8[认证授权<br/>SysAuthController]
        SYS9[验证码<br/>SysCaptchaController]
        SYS10[路由管理<br/>SysRouterController]
    end
    
    subgraph "服务层 Service"
        SVC1[用户服务<br/>ISysUserService]
        SVC2[角色服务<br/>ISysRoleService]
        SVC3[菜单服务<br/>ISysMenuService]
        SVC4[部门服务<br/>ISysDeptService]
        SVC5[权限服务<br/>ISysPermissionService]
        SVC6[会话服务<br/>ISysSessionService]
        SVC7[缓存服务<br/>ISysCacheService]
        SVC8[认证服务<br/>ISysAuthService]
        SVC9[验证码服务<br/>ISysCaptchaService]
        SVC10[路由服务<br/>ISysRouterService]
    end
    
    subgraph "数据访问层 Mapper"
        MAP1[用户Mapper<br/>SysUserMapper]
        MAP2[角色Mapper<br/>SysRoleMapper]
        MAP3[菜单Mapper<br/>SysMenuMapper]
        MAP4[部门Mapper<br/>SysDeptMapper]
        MAP5[权限Mapper<br/>SysPermissionMapper]
        MAP6[用户角色Mapper<br/>SysUserRoleMapper]
        MAP7[用户部门Mapper<br/>SysUserDeptMapper]
        MAP8[用户权限Mapper<br/>SysUserPermMapper]
    end
    
    SYS1 --> SVC1
    SYS2 --> SVC2
    SYS3 --> SVC3
    SYS4 --> SVC4
    SYS5 --> SVC5
    SYS6 --> SVC6
    SYS7 --> SVC7
    SYS8 --> SVC8
    SYS9 --> SVC9
    SYS10 --> SVC10
    
    SVC1 --> MAP1
    SVC2 --> MAP2
    SVC3 --> MAP3
    SVC4 --> MAP4
    SVC5 --> MAP5
    SVC1 --> MAP6 & MAP7 & MAP8
```

---

## 六、权限系统架构

```mermaid
graph TB
    subgraph "权限模型 Permission Model"
        PM1[用户 User]
        PM2[角色 Role]
        PM3[权限 Permission]
        PM4[菜单 Menu]
        PM5[部门 Dept]
        PM6[用户组 UserGroup]
        PM7[角色组 RoleGroup]
        PM8[权限组 PermGroup]
        PM9[部门组 DeptGroup]
    end
    
    subgraph "权限关系 Relations"
        R1[用户-角色<br/>N:N]
        R2[角色-权限<br/>N:N]
        R3[用户-权限<br/>N:N 直接授权]
        R4[用户-部门<br/>N:N]
        R5[用户-用户组<br/>N:N]
        R6[角色-角色组<br/>N:N]
        R7[权限-权限组<br/>N:N]
        R8[部门-部门组<br/>N:N]
    end
    
    subgraph "权限控制 Access Control"
        AC1[接口权限<br/>@Permission]
        AC2[字段权限<br/>@FieldPermission]
        AC3[数据权限<br/>@DataScope]
        AC4[菜单权限<br/>Menu Control]
    end
    
    PM1 --> R1 --> PM2
    PM2 --> R2 --> PM3
    PM1 --> R3 --> PM3
    PM1 --> R4 --> PM5
    PM1 --> R5 --> PM6
    PM2 --> R6 --> PM7
    PM3 --> R7 --> PM8
    PM5 --> R8 --> PM9
    
    PM3 --> AC1 & AC2 & AC3
    PM4 --> AC4
```

---

## 七、数据权限架构

```mermaid
graph LR
    subgraph "数据权限类型 DataScope Types"
        DS1[全部数据<br/>ALL]
        DS2[本部门数据<br/>DEPT]
        DS3[本部门及下级<br/>DEPT_AND_CHILD]
        DS4[仅本人数据<br/>SELF]
        DS5[自定义数据<br/>CUSTOM]
    end
    
    subgraph "权限处理流程"
        P1[请求进入]
        P2[检查@DataScope注解]
        P3[获取用户权限范围]
        P4[构建SQL过滤条件]
        P5[MyBatis拦截器]
        P6[动态添加WHERE条件]
        P7[执行SQL]
        P8[返回过滤后数据]
    end
    
    P1 --> P2
    P2 --> P3
    P3 --> DS1 & DS2 & DS3 & DS4 & DS5
    DS1 & DS2 & DS3 & DS4 & DS5 --> P4
    P4 --> P5
    P5 --> P6
    P6 --> P7
    P7 --> P8
```

---

## 八、安全防护体系

```mermaid
graph TB
    subgraph "请求安全 Request Security"
        RS1[CORS跨域防护<br/>CorsFilter]
        RS2[XSS攻击防护<br/>XssFilter]
        RS3[SQL注入防护<br/>SqlInjectionFilter]
        RS4[CSRF防护<br/>CsrfFilter]
    end

    subgraph "认证安全 Authentication Security"
        AS1[JWT Token认证<br/>TokenAuthenticationFilter]
        AS2[Token刷新机制<br/>RefreshToken]
        AS3[会话管理<br/>SessionHelper]
        AS4[单点登录<br/>SSO]
        AS5[多端登录控制<br/>MultiDevice]
    end
    
    subgraph "数据安全 Data Security"
        DS1[API加密解密<br/>ApiEncryptFilter]
        DS2[RSA非对称加密<br/>RsaCryptoHelper]
        DS3[密码加密<br/>BCrypt]
        DS4[字段脱敏<br/>@FieldPermission]
        DS5[敏感数据加密<br/>AES]
    end
    
    subgraph "权限安全 Authorization Security"
        PS1[接口权限校验<br/>@Permission]
        PS2[角色权限校验<br/>@RequiresRoles]
        PS3[数据权限过滤<br/>@DataScope]
        PS4[字段权限控制<br/>FieldPermission]
    end
    
    subgraph "审计安全 Audit Security"
        AU1[访问日志<br/>AccessLog]
        AU2[操作日志<br/>OperationLog]
        AU3[异常日志<br/>ErrorLog]
        AU4[登录日志<br/>LoginLog]
    end
```

---

## 九、缓存架构

```mermaid
graph TB
    subgraph "缓存层次 Cache Hierarchy"
        L1[一级缓存<br/>MyBatis本地缓存]
        L2[二级缓存<br/>Redis分布式缓存]
        L3[三级缓存<br/>本地内存缓存Caffeine]
    end
    
    subgraph "缓存策略 Cache Strategy"
        CS1[缓存穿透防护<br/>空值缓存]
        CS2[缓存击穿防护<br/>分布式锁]
        CS3[缓存雪崩防护<br/>过期时间随机化]
        CS4[热点数据<br/>永不过期]
    end
    
    subgraph "缓存应用 Cache Application"
        CA1[用户会话缓存<br/>Session Cache]
        CA2[权限数据缓存<br/>Permission Cache]
        CA3[字典数据缓存<br/>Dict Cache]
        CA4[验证码缓存<br/>Captcha Cache]
        CA5[限流计数器<br/>Rate Limiter]
    end
    
    L1 --> L2
    L2 --> L3
    CS1 & CS2 & CS3 & CS4 --> L2
    L2 --> CA1 & CA2 & CA3 & CA4 & CA5
```

---

## 十、数据库架构

```mermaid
erDiagram
    SYS_USER ||--o{ SYS_USER_ROLE : "用户角色关联"
    SYS_USER ||--o{ SYS_USER_DEPT : "用户部门关联"
    SYS_USER ||--o{ SYS_USER_PERM : "用户权限关联"
    SYS_USER ||--o{ SYS_USER_GROUP : "用户组关联"
    SYS_USER ||--o{ SYS_SESSION : "用户会话"
    
    SYS_ROLE ||--o{ SYS_USER_ROLE : "角色用户关联"
    SYS_ROLE ||--o{ SYS_ROLE_PERM : "角色权限关联"
    SYS_ROLE ||--o{ SYS_ROLE_GROUP : "角色组关联"
    
    SYS_PERMISSION ||--o{ SYS_ROLE_PERM : "权限角色关联"
    SYS_PERMISSION ||--o{ SYS_USER_PERM : "权限用户关联"
    SYS_PERMISSION ||--o{ SYS_PERM_GROUP : "权限组关联"
    
    SYS_DEPT ||--o{ SYS_USER_DEPT : "部门用户关联"
    SYS_DEPT ||--o{ SYS_DEPT : "部门树形结构"
    SYS_DEPT ||--o{ SYS_DEPT_GROUP : "部门组关联"
    
    SYS_MENU ||--o{ SYS_MENU : "菜单树形结构"
    SYS_MENU ||--o{ SYS_ROLE_MENU : "菜单角色关联"
    
    SYS_USER {
        bigint user_id PK "用户ID"
        varchar username "用户名"
        varchar password "密码"
        varchar nickname "昵称"
        varchar email "邮箱"
        varchar phone "手机号"
        tinyint status "状态"
        datetime create_time "创建时间"
    }
    
    SYS_ROLE {
        bigint role_id PK "角色ID"
        varchar role_code "角色编码"
        varchar role_name "角色名称"
        int sort "排序"
        tinyint status "状态"
    }
    
    SYS_PERMISSION {
        bigint perm_id PK "权限ID"
        varchar perm_code "权限编码"
        varchar perm_name "权限名称"
        varchar perm_type "权限类型"
        varchar resource "资源标识"
    }
    
    SYS_DEPT {
        bigint dept_id PK "部门ID"
        bigint parent_id FK "父部门ID"
        varchar dept_name "部门名称"
        int sort "排序"
        tinyint status "状态"
    }
    
    SYS_MENU {
        bigint menu_id PK "菜单ID"
        bigint parent_id FK "父菜单ID"
        varchar menu_name "菜单名称"
        varchar path "路由路径"
        varchar component "组件路径"
        int sort "排序"
    }
```

---

## 十一、技术栈详情

```mermaid
mindmap
  root((JunoYi技术栈))
    后端框架
      Spring Boot 3.5.0
      Spring MVC
      Spring Security
      Spring AOP
    数据访问
      MyBatis-Plus 3.5.7
      Druid连接池
      动态数据源
      分页插件
    缓存中间件
      Redis 7.0+
      Redisson
      Lock4j分布式锁
    安全组件
      JWT Token
      RSA加密
      BCrypt密码
      XSS防护
      SQL注入防护
    工具库
      Hutool 5.8.23
      Lombok
      MapStruct
      FastJson2
    文档工具
      SpringDoc OpenAPI
      Knife4j UI
    日志框架
      Logback
      自定义日志
    其他组件
      EasyExcel
      验证码
      IP2Region
```

---

## 十二、部署架构

### 开发环境（单机部署）

```mermaid
graph TB
    subgraph "开发机器"
        DEV1[JunoYi Server<br/>localhost:7588]
        DEV2[(MySQL<br/>localhost:3306)]
        DEV3[(Redis<br/>localhost:6379)]
        DEV4[本地文件存储<br/>./temp/logs]
    end
    
    DEV1 --> DEV2
    DEV1 --> DEV3
    DEV1 --> DEV4
```

### 生产环境（可选集群部署）

```mermaid
graph TB
    subgraph "负载均衡层（可选）"
        LB[Nginx<br/>反向代理]
    end
    
    subgraph "应用服务器集群"
        APP1[JunoYi Server 1<br/>8080]
        APP2[JunoYi Server 2<br/>8080]
        APP3[JunoYi Server 3<br/>8080]
    end
    
    subgraph "数据库集群"
        DB1[(MySQL Master<br/>主库)]
        DB2[(MySQL Slave<br/>从库)]
    end
    
    subgraph "缓存集群"
        REDIS1[(Redis Master)]
        REDIS2[(Redis Slave)]
    end
    
    subgraph "文件存储"
        FS1[NFS共享存储]
        FS2[OSS对象存储]
    end
    
    subgraph "监控告警"
        MON1[Prometheus]
        MON2[Grafana]
        MON3[ELK日志]
    end
    
    LB --> APP1 & APP2 & APP3
    APP1 & APP2 & APP3 --> DB1
    DB1 --> DB2
    APP1 & APP2 & APP3 --> REDIS1
    REDIS1 --> REDIS2
    APP1 & APP2 & APP3 --> FS1 & FS2
    APP1 & APP2 & APP3 --> MON1
    MON1 --> MON2
    APP1 & APP2 & APP3 --> MON3
```

**说明**：
- 单体架构支持单机部署（开发/小型项目）
- 也支持多实例集群部署（生产环境/高可用）
- 通过 Nginx 实现负载均衡和高可用
- 数据库和缓存可配置主从复制

---

## 十三、核心特性总结

### 1. 单体架构优势
- **部署简单**：一个 JAR 包即可运行
- **开发高效**：无需考虑分布式事务和服务调用
- **调试方便**：本地即可完整调试
- **成本低**：适合中小型项目，无需复杂基础设施
- **可扩展**：支持水平扩展（多实例部署）

### 2. 模块化设计
- 采用 Maven 多模块架构
- 模块职责清晰，低耦合高内聚
- 支持模块独立开发和测试
- 为未来微服务化预留空间

### 3. 安全防护体系
- 多层次安全防护（请求、认证、数据、权限）
- 端到端加密通信（RSA + AES）
- XSS、SQL注入、CSRF 防护
- 完善的审计日志

### 4. 混合权限模型
- 用户、角色、权限、菜单完全解耦
- 支持用户直接授权
- 支持分组管理（用户组、角色组、权限组、部门组）
- 数据权限精细控制（全部/部门/部门及下级/本人/自定义）

### 5. 高性能缓存
- 多级缓存架构（MyBatis 本地缓存 + Redis 分布式缓存）
- 缓存穿透/击穿/雪崩防护
- 分布式锁支持（Redisson + Lock4j）
- 热点数据预加载

### 6. 开发效率
- 统一异常处理
- 统一返回格式
- 对象自动转换（MapStruct）
- API文档自动生成（SpringDoc + Knife4j）
- 代码生成器

### 7. 可扩展性
- 插件化设计
- 事件驱动架构（Spring Event）
- 动态数据源支持
- 支持多端接入（Web/移动端/小程序/桌面端）

### 8. 架构演进路径
```
当前：单体架构（前后端分离版）
  ↓
未来：多租户版（SaaS 架构）
  ↓
未来：微服务版（Spring Cloud）
```

---

## 十四、版本信息

- **当前版本**: 0.3.4-alpha
- **Java版本**: 21
- **Spring Boot**: 3.5.0
- **MyBatis-Plus**: 3.5.7
- **文档更新**: 2026-01-19

---

**文档说明**: 本架构图使用 Mermaid 语法绘制，可在支持 Mermaid 的 Markdown 编辑器中查看完整效果。
