# 后端服务

基于 Spring Boot 3.3.x 的后端服务，提供生猪健康管理相关 API 与 AI 能力。

## 技术栈
- Java 21 + Maven
- Spring Boot、MyBatis-Plus、Sa-Token
- MySQL、Redis、MinIO
- Knife4j / OpenAPI
- Spring AI（DeepSeek / Ollama）

## 快速开始
```bash
mvn spring-boot:run
```

## 关键配置
- 配置文件：`src/main/resources/application.yml`
- 服务端口：`19999`
- 数据库初始化：导入 `SQL/pig_health_smart_medicine.sql`
- AI 密钥：设置环境变量 `DEEPSEEK_API_KEY`

## 接口文档
- Swagger/Knife4j：`/swagger-ui.html`
