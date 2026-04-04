## EnvironmentPlan: Pig Health Management 容器化交接稿

### 1) 对话背景与目标
用户目标：在全新 macOS 上实现项目可复现部署，并形成可用于比赛提交的“完整配置方案”。
初始想法：把全部依赖封装到 Docker 并考虑公开发布。
后续关键变更：用户明确未来将本地 Ollama 改为开放平台 API（优先 DeepSeek），因此方案已从“本地模型默认”调整为“外部 API 默认 + 本地 Ollama 可选”。

### 2) 当前已确认决策（最终口径）
- 默认 AI 模式：外部 API（不默认启动 Ollama）。
- 首选外部提供方：DeepSeek 官方 API。
- 交付形态：以 docker-compose 方案为主。
- 前端范围：包含 admin + web 两套前端容器化。
- 发布目标：同时支持 Docker Hub + GHCR。
- 保留扩展：提供 Ollama overlay compose 作为备用演示路径。
- miniapp：不做运行时容器化，仅在文档说明其构建/联调方式。

### 3) 已调研到的现状约束（供后续实现参考）
- 后端端口已确认是 19999。
- 后端配置文件在 backend/src/main/resources/application.yml，当前数据库、Redis、Ollama 等多为本地地址写法。
- 前端存在后端地址硬编码：
  - frontend/admin/src/axios.js
  - frontend/web/src/axios.js
- 启动脚本 scripts/start_services.sh 含开发者绝对路径，不能用于跨机器交付。
- 项目当前无现成 Dockerfile / docker-compose（需新建）。

### 4) 推荐架构（交付给比赛评审）
默认模式（推荐演示）：
- backend + mysql + redis + admin-nginx + web-nginx
- backend 调用 DeepSeek API（通过环境变量注入 API key 与 base url）

可选扩展模式（备用演示）：
- 在默认模式基础上叠加 ollama 服务
- 使用 compose overlay 切换为本地模型调用

### 5) 实施计划（可直接执行）
1. 配置分层：引入 AI_PROVIDER（deepseek/agent-gateway/ollama）与相关环境变量（API URL、KEY、模型、超时）。
2. 后端 AI 适配层：抽象统一接口，至少提供 DeepSeek 实现 + Ollama 实现，业务层只依赖接口。
3. 编排文件：
   - docker-compose.yml（默认外部 API）
   - docker-compose.ollama.yml（可选本地模型覆盖）
4. 容器化基础服务：backend/mysql/redis 健康检查、数据卷、启动顺序、初始化 SQL。
5. 前端容器化：admin/web 多阶段构建 + Nginx 静态托管，统一 /api 代理 backend。
6. 安全与配置模板：补齐 .env.example，明确密钥不入库，真实值走本地 .env 或 CI secrets。
7. 比赛文档：提供“默认部署”“ollama 备用部署”“常见故障与回退”三块内容。
8. 发布流程：定义 tag 策略并覆盖 Docker Hub + GHCR 推送与拉取验证。

### 6) 关键文件清单（高优先级）
- backend/src/main/resources/application.yml
- backend/src/main/java/com/linyi/pig/config/OllamaConfig.java
- backend/src/main/java/com/linyi/pig/service/impl/KnowledgeInitializer.java
- frontend/admin/src/axios.js
- frontend/web/src/axios.js
- frontend/web/vite.config.js
- doc/SQL/pig_health_smart_medicine.sql
- README.md
- docker-compose.yml（待创建）
- docker-compose.ollama.yml（待创建）
- .env.example（待创建）
- backend/Dockerfile（待创建）
- frontend/admin/Dockerfile（待创建）
- frontend/web/Dockerfile（待创建）

### 7) 验证闭环（验收标准）
- 默认模式：docker compose up 后可登录、可访问主要页面、AI 问诊可返回。
- 扩展模式：叠加 ollama compose 后，问诊在本地模型链路也可用。
- 故障回退：当 DeepSeek key 无效时，能给出可读错误并可切换到 Ollama 模式继续演示。
- 新机复现：全新 macOS（仅 Docker Desktop + .env）可按文档启动。
- 安全合规：仓库无真实密钥，文档包含敏感信息处理规范。

### 8) 风险与注意事项
- 若 KnowledgeInitializer 强依赖本地 embedding，需同步改为 provider 可切换，否则默认外部 API 模式可能受阻。
- 比赛现场网络不稳时，外部 API 可能超时，建议准备 Ollama 备用模式或离线演示脚本。
- 前端若未统一走 /api，容易出现跨域与环境地址漂移问题。

### 9) 给下一个模型的执行指令建议
- 先实现“配置分层 + AI_PROVIDER 抽象”，再落 Docker 与文档。
- 保持小步提交：每步可构建、可启动、可验证。
- 实现后先跑默认模式，再跑 ollama overlay，最后补发布脚本与 README 交付章节。
