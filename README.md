<div align="center">
    <img width="160" height="160" alt="JunoYi Logo" src="docs/img/logo.png" />
</div>

<div align="center">

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.9-blue.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-0.3.0--alpha-yellow.svg)](https://github.com/Juno-Yi/JunoYi)

## JunoYi 企业级开发框架
**一个安全内建、简洁优雅的 Java 企业级开发框架**

[特性](#-核心特性) • [快速开始](#-快速开始) • [文档](#-文档) • [架构](#-项目架构) • [贡献](#-贡献指南) • [联系我们](#-联系我们)

🌐 [官网](https://framework.junoyi.com) • 📖 [文档站](https://doc.framework.junoyi.com) • 🎮 [演示站](https://demo.junoyi.com)

</div>

---

> ⚠️ **Alpha 内测版本声明**
> 
> 当前版本为 **0.3.0-alpha**，属于内部测试版本。功能仍在持续开发和完善中，API 可能会有变动。欢迎试用并反馈问题，但暂不建议用于生产环境。
> 
> 📌 **相关链接**
> - 🌐 官网：https://framework.junoyi.com
> - 📖 文档站：https://doc.framework.junoyi.com  
> - 🎮 演示站：https://demo.junoyi.com
> 
> 演示站账号：
> 
> 超级管理员 - 账号：super_admin 密码：admin123
> 
> 用户管理员 - 账号：admin 密码：admin123
> 
> 用户1 - 账号：user1 密码：admin123

---

## �️ 版本绍规划

项目持续更新中，后续版本规划：

| 版本 | 说明 | 状态 |
|------|------|------|
| 单体版 | 当前版本，适合中小型项目 | 🚧 开发中 |
| 多租户版 | 支持 SaaS 多租户架构 | 📋 规划中 |
| 微服务版 | 基于 Spring Cloud 的分布式架构 | 📋 规划中 |

📝 更新日志请查看文档站：https://doc.framework.junoyi.com

---

## 📖 项目介绍

JunoYi（钧逸）是一个基于 **Spring Boot 3.5.0** 和 **Java 21** 打造的现代化企业级应用开发框架。提供更全面、更成熟、更可扩展的基础设施能力，专为现代企业级业务研发而生。

### ✨ 核心优势
- **企业级基础设施完善 :** 内置统一规范的架构设计、模块划分与扩展机制，显著降低项目搭建与长期维护成本。
- **专注业务，减少重复造轮子 :** 框架层承担通用能力建设，让开发者将精力集中在业务本身，而非基础防护与重复逻辑。
- **多端原生支持，一套代码多端适配 :** 天然支持 Web前台 / Web后台 / 移动端 / 小程序 / 桌面端多端接入，统一接口规范，降低多端协作成本。
- **混合权限模型，打破传统强耦合设计 :** 权限、角色、菜单完全解耦，不再被"菜单即权限"的旧范式限制，更符合真实企业业务场景。
- **安全能力内置，端到端加密通信 :** 内置接口级安全防护机制，实现端到端数据加密，让系统从架构层面更安全、更可靠。
- **面向长期演进的脚手架设计 :** 支持模块化扩展、插件化能力演进，适配中大型项目的持续迭代需求。

---

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+

### 启动步骤

```bash
# 1. 克隆项目
git clone https://github.com/Juno-Yi/JunoYi.git

# 2. 导入数据库
# 执行 sql/ 目录下的 SQL 脚本

# 3. 修改配置
# 编辑 junoyi-server/src/main/resources/application.yml
# 配置数据库和 Redis 连接信息

# 4. 启动项目
mvn spring-boot:run -pl junoyi-server
```

---

## 📚 文档

| 文档 | 说明 |
|------|------|
| [项目架构说明](docs/项目架构说明.md) | 整体架构设计与模块划分 |
| [模块开发指南](docs/模块开发指南.md) | 如何开发新的业务模块 |
| [权限系统设计文档](docs/JunoYi权限系统设计文档.md) | 混合权限模型详解 |
| [API文档使用指南](docs/JunoYi%20API文档使用指南.md) | Knife4j 接口文档配置 |
| [日志框架使用说明](docs/JunoYi日志框架使用说明.md) | 日志配置与使用 |
| [对象转换使用指南](docs/对象转换使用指南.md) | MapStruct 对象转换 |
| [滑块验证码前端使用指南](docs/滑块验证码前端使用指南.md) | 验证码集成说明 |
| [前端API加密解密示例](docs/前端API加密解密示例.md) | 端到端加密实现 |

---

## 🏗️ 项目架构

```
JunoYi
├── junoyi-dependencies        # 依赖版本管理
├── junoyi-framework           # 框架核心模块
│   ├── junoyi-framework-core          # 核心工具类
│   ├── junoyi-framework-web           # Web 基础设施
│   ├── junoyi-framework-security      # 安全认证
│   ├── junoyi-framework-permission    # 权限控制
│   ├── junoyi-framework-datasource    # 数据源配置
│   ├── junoyi-framework-redis         # Redis 缓存
│   ├── junoyi-framework-captcha       # 验证码
│   ├── junoyi-framework-api-doc       # API 文档
│   ├── junoyi-framework-log           # 日志框架
│   └── ...
├── junoyi-module              # 业务模块
│   ├── junoyi-module-system           # 系统管理
│   └── junoyi-module-demo             # 示例模块
├── junoyi-module-api          # 模块 API 定义
├── junoyi-server              # 启动入口
└── junoyi-ui                  # 前端项目
```

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

## 📞 联系我们

如果你在使用过程中遇到问题，或者有任何建议，欢迎通过以下方式联系我们：

- **QQ 群**：1074033133
- **邮箱**：eatfan0921@163.com

<div align="center">
    <img width="200" src="docs/img/QQ群.jpg" alt="QQ群二维码" />
    <p>扫码加入 QQ 群</p>
</div>

---

## ☕ 捐赠赞助

如果这个项目对你有帮助，欢迎请作者喝杯咖啡 ☕

<div align="center">
<table>
<tr>
<td align="center">
    <img width="200" src="docs/img/微信收款.JPG" alt="微信收款码" /><br/>
    <b>微信</b>
</td>
<td align="center">
    <img width="200" src="docs/img/支付宝收款.JPG" alt="支付宝收款码" /><br/>
    <b>支付宝</b>
</td>
</tr>
</table>
</div>

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

[![Star History Chart](https://api.star-history.com/svg?repos=Juno-Yi/JunoYi&type=date&legend=top-left)](https://www.star-history.com/#Juno-Yi/JunoYi&type=date&legend=top-left)

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！**

Made with ❤️ by JunoYi Team

</div>
