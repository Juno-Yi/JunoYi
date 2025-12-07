# JunoYi 钧逸后台管理框架 （开发中。。。）

<div align="center">

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.5-blue.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

</div>

## 📖 项目介绍

JunoYi 是基于 Spring Boot 3.2 打造的一体化企业级应用开发框架，采用 模块化 + 插件化 的架构理念，通过 Maven 多模块实现核心能力与业务功能的完全解耦，为企业提供 高性能、可扩展、工程化水准的后端基础框架。

框架内置丰富的基础设施，包括：
•	自动代码生成器（Code Generator）：支持实体、Mapper、Service、Controller 及前端 Vue 文件一键生成，实现快速原型搭建与标准化开发。
•	自研 Juno Starter 体系：提供启动日志、模块扫描、模块初始化、Banner 控制、包扫描策略、全局配置绑定等能力，实现模块级热插拔与自动装配。
•	MyBatis Plus 深度集成：支持自动分页、通用 CRUD、Lambda 链式调用与自定义 SQL 扩展，提高数据访问效率与开发体验。
•	统一日志链路（Juno Log）：包含请求链路追踪、业务日志收集、异常日志增强，实现生产级应用的可观察性与可审计性。
•	统一响应 / 统一异常处理机制：规范系统交互协议，简化前后端协作成本。
•	RBAC 权限模型与令牌机制：支持多端认证、动态权限、接口级与菜单级权限。
•	模块化系统设计：支持业务按功能拆分为独立模块，可随时扩展、复用或部署。

JunoYi 致力于提供一套 可继承、可裁剪、可扩展的企业级后端框架，帮助团队从繁琐的基础工程中解放出来，将更多时间专注在实际业务价值的构建。

### ✨ 核心特性

- 🎯 **统一版本管理** - 通过 dependencies 模块统一管理所有依赖版本
- 🏗️ **模块化架构** - API 与实现分离，职责清晰，易于维护
- 🔧 **框架二次封装** - 对常用框架进行封装，开箱即用
- 📦 **易于扩展** - 只需在 module 和 module-api 中添加新模块即可扩展功能
- 🚀 **快速开发** - 提供完整的代码模板和开发指南

### 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|-----|------|------|
| Java | 21 | JDK 版本 |
| Spring Boot | 3.2.0 | 基础框架 |
| MyBatis Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0.33 | 数据库 |
| Druid | 1.2.20 | 数据库连接池 |
| Lombok | 1.18.30 | 代码简化工具 |
| Hutool | 5.8.23 | Java 工具类库 |
| FastJson2 | 2.0.43 | JSON 处理 |
| Knife4j | 4.3.0 | 接口文档 |
| JWT | 0.12.3 | 认证授权 |
| MapStruct | 1.5.5.Final | 对象映射 |

## 📁 项目结构

```
JunoYi/
├── junoyi-dependencies      # 依赖管理模块（统一版本控制）
├── junoyi-common           # 公共模块（工具类、常量、枚举）
├── junoyi-framework/       # 框架核心封装模块
│   ├── framework-core      #   核心功能封装
│   └── framework-web       #   Web 相关封装
├── junoyi-module-api/      # 功能模块 API（对外接口定义）
│   └── module-system-api/  #   系统模块 API
│       ├── domain/         #   领域对象
│       │   ├── entity/     #   实体类（数据库映射）
│       │   ├── vo/         #   视图对象（返回前端）
│       │   └── bo/         #   业务对象（业务传输）
│       ├── service/        #   服务接口
│       └── mapper/         #   数据访问接口
├── junoyi-module/          # 功能模块实现
│   └── module-system/      #   系统模块实现
│       ├── controller/     #   控制器
│       ├── service.impl/   #   服务实现
│       └── convert/        #   对象转换
├── junoyi-server           # 启动模块（应用入口）
└── docs/                   # 项目文档
    ├── 项目架构说明.md
    ├── 模块开发指南.md
    ├── 快速参考.md
    └── 修复说明.md
```

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+

### 构建项目

```bash
# 克隆项目
git clone https://github.com/yourusername/JunoYi.git
cd JunoYi

# 构建项目
mvn clean install

# 跳过测试构建
mvn clean install -DskipTests
```

### 运行项目

```bash
# 方式1: 使用 Maven 运行
cd junoyi-server
mvn spring-boot:run

# 方式2: 打包后运行
cd junoyi-server
mvn clean package
java -jar target/junoyi-server.jar
```

## 📚 文档

- [项目架构说明](docs/项目架构说明.md) - 了解项目整体架构和设计理念
- [模块开发指南](docs/模块开发指南.md) - 学习如何创建和开发新模块
- [快速参考](docs/快速参考.md) - 快速查找常用配置和命令
- [修复说明](docs/修复说明.md) - 查看项目修复记录

## 🎯 核心功能

### 1. 统一版本管理

所有依赖版本在 `junoyi-dependencies` 模块中统一管理：

```xml
<!-- 只需修改根 pom.xml 即可升级整个项目版本 -->
<properties>
    <revision>1.0.0</revision>
</properties>
```

### 2. 模块化开发

- **API 模块** - 定义接口和领域对象
- **实现模块** - 实现具体业务逻辑
- **框架模块** - 提供通用功能封装
- **公共模块** - 提供工具类和常量

### 3. 二次开发

只需在 `junoyi-module` 和 `junoyi-module-api` 中添加新模块即可扩展功能，详见 [模块开发指南](docs/模块开发指南.md)。

## 🔧 模块说明

### junoyi-dependencies
统一管理所有第三方和内部模块的版本，确保依赖版本一致性。

### junoyi-common
存放项目中通用的工具类、常量、枚举、异常定义等。

### junoyi-framework
对 Spring Boot、MyBatis Plus 等框架进行二次封装：
- **framework-core** - 统一返回结果、全局异常处理、基础配置
- **framework-log** - 日志封装
- **framework-web** - Web 配置、跨域、接口文档、安全认证

### junoyi-module-api
定义各功能模块的对外接口、领域对象，实现接口与实现分离。

### junoyi-module
实现 module-api 中定义的接口，包含具体的业务逻辑。

### junoyi-server
应用程序的启动入口，聚合所有功能模块。

## 📝 开发规范

### 包名规范
```
com.junoyi.common                    # 公共模块
com.junoyi.framework.core            # 框架核心
com.junoyi.framework.web             # 框架 Web
com.junoyi.module.xxx.api            # 模块 API
com.junoyi.module.xxx                # 模块实现
```

### 依赖规则
- ✅ module → module-api
- ✅ module → framework-web
- ✅ module-api → common
- ❌ module-api → framework
- ❌ module 之间互相依赖

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建新分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 📮 联系方式


## 🙏 鸣谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis Plus](https://baomidou.com/)
- [Hutool](https://hutool.cn/)
- [Knife4j](https://doc.xiaominfo.com/)

---

⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！
