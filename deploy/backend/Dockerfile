# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .

# 项目 BOM 独立于根 POM；先安装到构建阶段的本地 Maven 仓库。
RUN mvn -B -ntp -f ara-dependencies/pom.xml -Dmaven.test.skip=true install \
    && mvn -B -ntp -pl ara-server -am -Dmaven.test.skip=true package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN mkdir -p /app/data/uploads /app/data/logs /app/config \
    && chown -R 10001:10001 /app
COPY --from=build --chown=10001:10001 /workspace/ara-server/target/ara-server.jar /app/app.jar
USER 10001:10001
EXPOSE 7588
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
