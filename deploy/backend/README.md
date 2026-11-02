# 后端 Docker 环境配置

`config/application-docker.yml` 保存三个环境共用的配置与环境变量引用；`env/*.env` 保存各环境的实际地址、账号和密钥。仓库只提交 `*.env.example`，真实 `*.env` 和 `config/certs/` 已被 Git 忽略。

## 准备环境文件

在仓库根目录执行，例如开发环境：

```sh
cp deploy/backend/env/dev.env.example deploy/backend/env/dev.env
openssl rand -hex 64
```

将生成的随机值填入 `JWT_SECRET`，并填写 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`、`REDIS_HOST` 等实际连接信息。测试和生产环境分别复制 `test.env.example`、`prod.env.example`，各自使用独立的数据库、Redis 和 JWT 密钥。`*.example` 中的 `example.invalid` 是占位地址，不能直接连接。

容器里的 `localhost` 指容器自身。数据库或 Redis 运行在 Docker 宿主机时可使用 `host.docker.internal`；运行在同一 Docker 网络时使用服务名。`REDIS_PASSWORD` 可以留空，前提是对应 Redis 不需要密码。`CORS_ALLOWED_ORIGINS` 填浏览器页面的完整 Origin，多个 Origin 用逗号分隔。

## 启动

```sh
docker compose --env-file deploy/backend/env/dev.env -f deploy/backend/compose.yaml up -d --build
```

测试或生产环境将命令中的 `dev.env` 改为 `test.env` 或 `prod.env`。`APP_ENV` 会选择相应的 `env/${APP_ENV}.env`，容器中启用 `docker,dev`、`docker,test` 或 `docker,prod` 配置。修改环境变量后重新创建容器：

```sh
docker compose --env-file deploy/backend/env/dev.env -f deploy/backend/compose.yaml up -d --force-recreate
```

`HOST_BIND_ADDRESS` 和 `HOST_PORT` 控制宿主机端口，示例默认只绑定回环地址。生产域名需由反向代理转发到相应端口。文件与日志分别保存在 Docker 卷的 `/app/data/uploads` 和 `/app/data/logs`，容器重建后仍保留。

## 可选集成

使用 OSS 或 MinIO 时设置 `FILE_STORAGE_TYPE`，再填写对应的 `OSS_*` 或 `MINIO_*`。企业微信、小程序和微信支付默认关闭；启用时在环境文件中取消对应变量的注释，并填写真实凭据。微信支付证书可放在 `deploy/backend/config/certs/`，容器内路径为 `/app/config/certs/`。

`API_ENCRYPT_ENABLED` 默认关闭。当前 `RsaCryptoHelper` 从 JAR 的 `keys/private.pem` 和 `keys/public.pem` 读取密钥，不会从 `.env` 读取 RSA 密钥。生产环境如要开启接口加密，需要先替换打包进 JAR 的密钥对，并更新前端公钥。`JWT_SECRET` 与这组 RSA 密钥是两种不同用途的密钥。
