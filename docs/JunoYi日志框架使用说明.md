# JunoYi日志框架使用说明

## 概述

JunoYi日志框架是一个基于Spring Boot和Logback的高性能日志解决方案，提供美化的日志输出、灵活的配置选项和强大的功能特性。

**注意：JunoYi日志框架始终启用，不支持禁用功能。**

## 🎨 主要特性

- **🌈 彩色日志输出**: 支持彩色和纯文本两种输出模式
- **📏 智能格式化**: 自动对齐、智能包名缩写、固定宽度显示
- **🎯 级别区分**: 不同日志级别使用不同颜色和背景
- **⚙️ 灵活配置**: 通过配置文件完全控制日志行为
- **🔧 高级异常**: 美观的异常堆栈显示
- **📊 MDC支持**: 彩色MDC上下文信息显示

## 🚀 快速开始

### 1. 添加依赖

在您的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>cn.junoyi</groupId>
    <artifactId>junoyi-framework-log</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 基础配置

在 `application.yml` 中添加基础配置：

```yaml
junoyi:
  log:
    enabled: true
    console:
      enabled: true
      color-enabled: true
      show-thread-name: true
      show-mdc: true
      show-class-name: true
      max-class-name-length: 20
```

### 3. 使用日志

```java
import cn.junoyi.framework.log.JunoYiLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    // 使用JunoYi框架日志
    private static final JunoYiLogger logger = JunoYiLogger.getLogger(UserService.class);
    
    // 或者使用标准SLF4J
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void createUser(String username) {
        logger.info("创建用户: {}", username);
        logger.warn("用户名可能重复: {}", username);
        logger.error("创建用户失败", e);
        
        // 标准SLF4J日志也会被JunoYi框架美化
        log.debug("调试信息: {}", username);
    }
}
```

## 📋 详细配置

### 完整配置示例

```yaml
junoyi:
  log:
    # 是否启用JunoYi日志框架
    enabled: true
    
    # 控制台输出配置
    console:
      enabled: true                    # 是否启用控制台输出
      color-enabled: true              # 是否启用彩色输出
      show-thread-name: true           # 是否显示线程名
      show-mdc: true                   # 是否显示MDC上下文
      show-class-name: true            # 是否显示类名
      max-class-name-length: 20        # 类名最大长度
    
    # 文件输出配置
    file:
      enabled: false                   # 是否启用文件输出
      path: "logs/junoyi.log"          # 日志文件路径
      max-size: "100MB"                # 单个文件最大大小
      max-history: 30                  # 保留历史文件数量
      total-size-cap: "1GB"            # 总文件大小限制
    
    # 日志级别配置
    level:
      root: WARN                       # 根日志级别
      junoyi: INFO                     # JunoYi框架日志级别
      spring: WARN                     # Spring框架日志级别
      mybatis: WARN                    # MyBatis日志级别
      sql: DEBUG                       # SQL日志级别
      custom: "com.example:DEBUG,org.apache:INFO"  # 自定义包级别
    
    # 异步日志配置
    async:
      enabled: false                   # 是否启用异步日志
      queue-size: 1024                 # 异步队列大小
      discarding-threshold: 0          # 丢弃阈值
      include-caller-data: false       # 是否包含调用者数据
```

### 配置说明

#### 控制台配置 (console)

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `enabled` | boolean | true | 是否启用控制台输出 |
| `color-enabled` | boolean | true | 是否启用彩色输出 |
| `show-thread-name` | boolean | true | 是否显示线程名 |
| `show-mdc` | boolean | true | 是否显示MDC上下文 |
| `show-class-name` | boolean | true | 是否显示类名 |
| `max-class-name-length` | int | 20 | 类名最大长度 |

#### 日志级别配置 (level)

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `root` | String | WARN | 根日志级别 |
| `junoyi` | String | INFO | JunoYi框架日志级别 |
| `spring` | String | WARN | Spring框架日志级别 |
| `mybatis` | String | WARN | MyBatis日志级别 |
| `sql` | String | DEBUG | SQL日志级别 |
| `custom` | String | - | 自定义包级别，格式：`包名:级别,包名:级别` |

支持的日志级别：`TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`

## 🎨 日志格式说明

### 标准日志格式

```
[时间戳] (线程名) 类名 [级别] 日志消息
```

### 实际示例

**彩色模式**：
```
[2025-11-25 13:49:15] (main     ) j.server.Test                  [INFO ] 测试日志系统
[2025-11-25 13:49:15] (main     ) j.s.controller.UserController  [ERROR] 用户登录失败
[2025-11-25 13:49:15] (task-1    ) j.s.service.OrderService      [WARN ]  订单处理异常
[2025-11-25 13:49:15] (main     ) j.s.dao.UserMapper            [DEBUG] 查询用户信息
[2025-11-25 13:49:15] (main     ) j.s.util.TraceUtil            [TRACE] 执行跟踪
```

**纯文本模式** (`color-enabled: false`)：
```
[2025-11-25 13:49:15] (main     ) j.server.Test                  [INFO ] 测试日志系统
[2025-11-25 13:49:15] (main     ) j.s.controller.UserController  [ERROR] 用户登录失败
[2025-11-25 13:49:15] (task-1    ) j.s.service.OrderService      [WARN ]  订单处理异常
[2025-11-25 13:49:15] (main     ) j.s.dao.UserMapper            [DEBUG] 查询用户信息
[2025-11-25 13:49:15] (main     ) j.s.util.TraceUtil            [TRACE] 执行跟踪
```

### 颜色说明

| 日志级别 | 背景色 | 文字色 | 示例 |
|----------|--------|--------|------|
| ERROR | 红色 | 白色粗体 | `[ERROR]` |
| WARN | 黄色 | 黑色粗体 | `[WARN ]` |
| INFO | 绿色 | 白色粗体 | `[INFO ]` |
| DEBUG | 蓝色 | 白色粗体 | `[DEBUG]` |
| TRACE | 紫色 | 白色粗体 | `[TRACE]` |

### 格式化特性

1. **时间戳**: 红色显示，格式为 `yyyy-MM-dd HH:mm:ss`
2. **线程名**: 绿色显示，智能简化长线程名
3. **类名**: 青色显示，智能包名缩写
4. **MDC**: 紫色显示，格式为 `[MDC: key=value,key=value]`
5. **消息**: 根据级别显示不同颜色
6. **异常**: 红色边框，美观的堆栈跟踪

## 🔧 高级用法

### MDC使用

```java
import org.slf4j.MDC;

public class OrderService {
    public void processOrder(String orderId, String userId) {
        MDC.put("orderId", orderId);
        MDC.put("userId", userId);
        
        try {
            logger.info("开始处理订单");
            // 业务逻辑
            logger.info("订单处理完成");
        } finally {
            MDC.clear();
        }
    }
}
```

**输出效果**：
```
[2025-11-25 13:49:15] (main     ) j.s.service.OrderService      [INFO ] [MDC: orderId=12345,userId=67890] 开始处理订单
```

### 自定义日志级别

```yaml
junoyi:
  log:
    level:
      custom: "com.example.controller:DEBUG,com.example.service:INFO,org.apache:WARN"
```

### 异步日志配置

```yaml
junoyi:
  log:
    async:
      enabled: true
      queue-size: 2048
      discarding-threshold: 0
      include-caller-data: true
```

## 🎯 最佳实践

### 1. 日志级别建议

- **生产环境**: 
  ```yaml
  level:
    root: WARN
    junoyi: INFO
    spring: WARN
    mybatis: WARN
    sql: WARN
  ```

- **开发环境**:
  ```yaml
  level:
    root: INFO
    junoyi: DEBUG
    spring: INFO
    mybatis: DEBUG
    sql: DEBUG
  ```

- **测试环境**:
  ```yaml
  level:
    root: INFO
    junoyi: INFO
    spring: WARN
    mybatis: INFO
    sql: INFO
  ```

### 2. 性能优化

- 生产环境建议关闭彩色输出：`color-enabled: false`
- 高并发场景启用异步日志：`async.enabled: true`
- 合理设置队列大小：`async.queue-size: 1024`

### 3. 日志规范

```java
// ✅ 推荐：使用参数化日志
logger.info("用户登录成功: userId={}, username={}", userId, username);

// ❌ 避免：字符串拼接
logger.info("用户登录成功: userId=" + userId + ", username=" + username);

// ✅ 推荐：异常日志包含堆栈
logger.error("用户登录失败: userId=" + userId, exception);

// ✅ 推荐：调试日志使用DEBUG级别
logger.debug("进入方法: processOrder, orderId={}", orderId);
```

## 🐛 常见问题

### Q1: 如何完全禁用日志输出？

```yaml
junoyi:
  log:
    enabled: false
```

### Q2: 如何只输出错误日志？

```yaml
junoyi:
  log:
    level:
      root: ERROR
      junoyi: ERROR
      spring: ERROR
      mybatis: ERROR
      sql: ERROR
```

### Q3: 如何自定义日志格式？

JunoYi日志框架使用固定的美观格式，如需自定义格式，可以通过修改 `JunoYiLogbackEncoder` 类实现。

### Q4: 为什么彩色不生效？

检查以下配置：
1. 确认 `color-enabled: true`
2. 确认终端支持ANSI颜色
3. 某些IDE可能不支持彩色输出

### Q5: 如何调整类名显示长度？

```yaml
junoyi:
  log:
    console:
      max-class-name-length: 30  # 调整为30字符
```

## 📞 技术支持

如有问题或建议，请联系：
- 邮箱: support@junoyi.com
- 文档: https://docs.junoyi.com/log
- GitHub: https://github.com/junoyi/junoyi-framework-log

---

*JunoYi日志框架 - 让日志更美观，让调试更高效！* 🎉
