# 后端环境配置（Docker / JAR）

`config/application-docker.yml` 保存三个环境共用的配置与环境变量引用，普通 JAR 部署也读取这份外置 YAML；`env/*.env` 保存各环境的实际地址、账号和密钥。仓库只提交 `*.env.example`，真实 `*.env`、`config/certs/` 和 JAR 运行数据目录已被 Git 忽略。

## 准备环境文件

在仓库根目录执行，例如开发环境：

```sh
cp deploy/backend/env/dev.env.example deploy/backend/env/dev.env
openssl rand -hex 64
```

将生成的随机值填入 `JWT_SECRET`，并填写 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`、`REDIS_HOST` 等实际连接信息。测试和生产环境分别复制 `test.env.example`、`prod.env.example`，各自使用独立的数据库、Redis 和 JWT 密钥。`*.example` 中的 `example.invalid` 是占位地址，不能直接连接。

容器里的 `localhost` 指容器自身。数据库或 Redis 运行在 Docker 宿主机时可使用 `host.docker.internal`；运行在同一 Docker 网络时使用服务名；连接远程服务器时填写实际主机名或 IP。无密码 Redis 不设置 `SPRING_DATA_REDIS_PASSWORD`；启用认证时，在对应环境文件中取消该行注释并填写密码。空密码会使 Redisson 尝试执行 `AUTH`，导致启动失败。跨域由 `CORS_ENABLED`、`CORS_ALLOW_CREDENTIALS`、`CORS_ALLOWED_ORIGINS`、`CORS_ALLOWED_METHODS`、`CORS_ALLOWED_HEADERS` 完整控制；后三项是逗号分隔的列表，Origin 要填写浏览器页面的完整来源地址。

Token 的请求头和单点登录开关分别由 `TOKEN_HEADER`、`TOKEN_SINGLE_LOGIN` 设置；五个平台各有独立的 `TOKEN_ACCESS_EXPIRE_*` 和 `TOKEN_REFRESH_EXPIRE_*`。有效期可以写成 `30m`、`2h`、`7d`。三个环境文件中的示例值保留了原配置的有效期，可分别修改。如果修改 `TOKEN_HEADER`，也要将新名称加入 `CORS_ALLOWED_HEADERS`。

## Docker 启动

```sh
docker compose --env-file deploy/backend/env/dev.env -f deploy/compose.yaml up -d --build
```

测试或生产环境将命令中的 `dev.env` 改为 `test.env` 或 `prod.env`。`APP_ENV` 会选择相应的 `env/${APP_ENV}.env`，容器中启用 `docker,dev`、`docker,test` 或 `docker,prod` 配置。修改环境变量后重新创建容器：

```sh
docker compose --env-file deploy/backend/env/dev.env -f deploy/compose.yaml up -d --force-recreate
```

`HOST_BIND_ADDRESS` 和 `HOST_PORT` 控制宿主机端口，示例默认只绑定回环地址。生产域名需由反向代理转发到相应端口。文件与日志分别保存在 Docker 卷的 `/app/data/uploads` 和 `/app/data/logs`，容器重建后仍保留。

## 普通 JAR 启动

打包后使用同一份环境模板，另存一个普通部署专用文件。例如生产环境：

```bash
cp deploy/backend/env/prod.env.example deploy/backend/env/prod-jar.env
# 编辑 prod-jar.env：填写数据库、Redis、JWT，并将数据库和 Redis 地址改为宿主机可访问的地址。
# 同时设置 LOG_FILE_PATH 和 FILE_LOCAL_BASE_PATH 为宿主机可写目录。
while IFS= read -r line || [[ -n "$line" ]]; do
  [[ -z "$line" || "$line" == \#* ]] && continue
  export "$line"
done < deploy/backend/env/prod-jar.env
mkdir -p "$LOG_FILE_PATH" "$FILE_LOCAL_BASE_PATH"
java -jar junoyi-server/target/junoyi-server.jar \
  --spring.profiles.active=prod \
  "--spring.config.location=classpath:/application.yml,file:$PWD/deploy/backend/config/application-docker.yml" \
  "--server.address=$HOST_BIND_ADDRESS" \
  "--server.port=$HOST_PORT"
```

开发和测试环境将 `prod` 换为 `dev` 或 `test`，并复制对应示例。命令逐行读取 `.env`，使用外置 `application-docker.yml` 覆盖 JAR 内默认配置，并以相应 Profile 启动。普通 JAR 部署不会自动读取 `.env`。示例中的 `host.docker.internal` 是容器访问宿主机的地址，普通部署通常应换为 `127.0.0.1` 或实际服务地址。

普通部署需在环境文件中设置 `LOG_FILE_PATH`、`FILE_LOCAL_BASE_PATH` 为宿主机可写目录。`HOST_BIND_ADDRESS`、`HOST_PORT` 用于普通部署的监听地址和端口。

## 可选集成

使用 OSS 或 MinIO 时设置 `FILE_STORAGE_TYPE`，再填写对应的 `OSS_*` 或 `MINIO_*`。企业微信、小程序和微信支付默认关闭；启用时在环境文件中取消对应变量的注释，并填写真实凭据。微信支付证书可放在 `deploy/backend/config/certs/`，容器内路径为 `/app/config/certs/`。

生产示例中 `API_ENCRYPT_ENABLED=true`，开发和测试示例为 `false`。Docker 与普通 JAR 部署均只通过这个变量切换接口加密；RSA 密钥仍按现有 JAR 内的加载逻辑管理，不由 `.env` 配置。
