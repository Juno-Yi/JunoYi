<div align="center">
    <img width="160" height="160" alt="Image" src="https://junoyi.eatfan.top/LOGO.png" />
</div>

<div align="center">

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.9-blue.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0-success.svg)](https://github.com/Juno-Yi/JunoYi)

## JunoYi 企业级开发框架
**一个现代化、模块化、高性能的企业级 Java 开发框架**

[特性](#-核心特性) • [快速开始](#-快速开始) • [文档](#-文档) • [架构](#-项目架构) • [贡献](#-贡献指南)

</div>

---

## 📖 项目介绍

JunoYi（钧逸）是一个基于 **Spring Boot 3.3.5** 和 **Java 21** 打造的现代化企业级应用开发框架。采用 **模块化 + 插件化** 的架构设计，通过 Maven 多模块实现核心能力与业务功能的完全解耦，为企业提供高性能、可扩展、工程化水准的后端基础设施。

### 🎯 设计理念

- **模块化优先** - 每个功能模块独立开发、独立测试、独立部署
- **接口与实现分离** - API 模块定义契约，实现模块专注业务
- **开箱即用** - 内置丰富的基础设施，零配置快速启动
- **可观测性** - 完善的日志、监控、链路追踪体系
- **安全第一** - 内置认证授权、API 加密、防护机制

### ✨ 核心特性

#### 🏗️ 架构特性
- **模块化架构** - API 与实现分离，职责清晰，易于维护和扩展
- **统一版本管理** - 通过 dependencies 模块统一管理所有依赖版本
- **热插拔设计** - 模块可随时添加、移除，不影响其他模块
- **多数据源支持** - 内置动态数据源切换，支持读写分离

#### 🔧 框架能力
- **自研日志框架** - 彩色输出、链路追踪、性能监控、异步日志
- **统一响应封装** - 标准化 API 响应格式，简化前后端协作
- **全局异常处理** - 统一异常捕获和处理，友好的错误提示
- **代码生成器** - 一键生成 Entity、Mapper、Service、Controller
- **API 文档集成** - Knife4j 接口文档，支持在线调试

#### 🔐 安全特性
- **JWT + Sa-Token** - 双重认证机制，支持多端登录
- **API 加密过滤器** - 请求/响应加密，保护敏感数据
- **权限控制** - 基于 RBAC 的权限模型，支持动态权限
- **白名单机制** - 灵活的路径白名单配置

#### 🚀 开发体验
- **零配置启动** - 开箱即用，无需复杂配置
- **热重载支持** - 开发环境自动重启，提升开发效率
- **完善的文档** - 详细的开发指南和 API 文档
- **代码规范** - 统一的代码风格和最佳实践

---

## 🛠️ 技术栈

### 核心框架

| 技术 | 版本    | 说明 |
|-----|-------|------|
| Java | 21    | JDK 版本 |
| Spring Boot | 3.5.0 | 基础框架 |
| Spring Boot Starter Web | 3.5.0 | Web 开发 |
| Spring Boot Starter AOP | 3.5.0 | 面向切面编程 |

### 数据访问

| 技术 | 版本 | 说明 |
|-----|------|------|

### 缓存与分布式

| 技术 | 版本 | 说明 |
|-----|------|------|


### 安全认证


### 工具库

---

## 📁 项目架构

### 模块结构

```

```

### 模块依赖关系

```
┌─────────────────┐
│  junoyi-server  │  ← 启动模块（聚合所有功能）
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
┌───▼──────┐  │
│  module  │  │  ← 功能模块实现
└───┬──────┘  │
    │         │
┌───▼──────┐  │
│module-api│  │  ← 功能模块 API
└───┬──────┘  │
    │         │
┌───▼──────┐  │
│framework │◄─┘  ← 框架核心
└───┬──────┘
    │
┌───▼──────┐
│  common  │      ← 公共模块
└──────────┘
```

---


### 5. 启动项目

```bash
# 方式1: 使用 Maven 运行
cd junoyi-server
mvn spring-boot:run

# 方式2: 打包后运行
cd junoyi-server
mvn clean package
java -jar target/junoyi-server.jar

# 方式3: 指定环境运行
java -jar target/junoyi-server.jar --spring.profiles.active=local
```

### 6. 访问应用

- **应用地址**: http://localhost:7588
- **接口文档**: http://localhost:7588/doc.html
- **健康检查**: http://localhost:7588/actuator/health

---

## 📚 文档


---

## 🎯 核心功能

### 1. 自研日志框架

JunoYi 内置了一套强大的日志框架，提供：

- ✅ **彩色输出** - 不同级别日志不同颜色，易于区分
- ✅ **链路追踪** - 自动生成 TraceID，追踪请求全链路
- ✅ **性能监控** - 记录方法执行时间，发现性能瓶颈
- ✅ **异步日志** - 支持异步写入，不阻塞主线程
- ✅ **MDC 上下文** - 自动记录用户、请求等上下文信息


---

## 🔧 配置说明


## 📝 开发规范

### 包名规范

```
com.junoyi.common                    # 公共模块
com.junoyi.framework.xxx             # 框架模块
com.junoyi.module.xxx.api            # 模块 API
com.junoyi.module.xxx                # 模块实现
```

### 依赖规则

- ✅ `module` → `module-api`
- ✅ `module` → `framework`
- ✅ `module-api` → `common`
- ❌ `module-api` → `framework`
- ❌ `module` 之间互相依赖

### 代码规范

- 使用 Lombok 简化代码
- 使用 MapStruct 进行对象转换
- Controller 只做参数校验和调用 Service
- Service 处理业务逻辑
- Mapper 只做数据访问

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 贡献流程

1. Fork 本项目
2. 创建新分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### 代码审查

所有 PR 都需要经过代码审查才能合并。请确保：

- 代码符合项目规范
- 添加了必要的测试
- 更新了相关文档
- 通过了所有 CI 检查

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 🙏 鸣谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot) - 基础框架
- [MyBatis Plus](https://baomidou.com/) - ORM 框架
- [Sa-Token](https://sa-token.cc/) - 认证授权
- [Redisson](https://redisson.org/) - Redis 客户端
- [Hutool](https://hutool.cn/) - Java 工具库
- [Knife4j](https://doc.xiaominfo.com/) - 接口文档

---

## 📮 联系方式

- **项目主页**: https://github.com/Juno-Yi/JunoYi
- **问题反馈**: https://github.com/Juno-Yi/JunoYi/issues
- **邮箱**: eatfan0921@gmail.com

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！**

Made with ❤️ by JunoYi Team

</div>

