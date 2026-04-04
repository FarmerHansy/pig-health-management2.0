# 项目快速启动指南

## ✅ 已完成

- ✅ **环境安装**: Java 21, Maven, MySQL 8.0, Redis, Node.js
- ✅ **数据库初始化**: `pig_health_smart_medicine` 已创建并导入数据
- ✅ **后端构建**: 项目已成功编译 (backend-1.0.0.jar)
- ✅ **前端依赖**: admin 和 web 前端依赖已安装
- ✅ **配置更新**: 前端 API 地址已更新为 `http://127.0.0.1:19999`

## 🚀 快速启动所有服务

在新终端中执行:

```bash
cd /Users/aas/Desktop/PigHealthSmartMedicine
./start_services.sh
```

## 🔌 当前运行的服务

| 服务 | 状态 | 访问地址 |
|------|------|----------|
| 后端 API | ✅ 运行中 | [http://localhost:19999](http://localhost:19999) |
| API 文档 | ✅ 可用 | [http://localhost:19999/doc.html](http://localhost:19999/doc.html) |
| 管理端前端 | ✅ 运行中 | [http://localhost:5173](http://localhost:5173) |
| Web 用户端 | ✅ 运行中 | [http://localhost:5174](http://localhost:5174) |
| MySQL | ✅ 运行中 | - |
| Redis | ✅ 运行中 | - |

## 📋 测试账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | 123456 | 管理员 |
| linyi | 123456 | 普通用户 |

## 📊 检查服务状态

```bash
./status_services.sh
```

## 🛑 停止所有服务

```bash
./stop_services.sh
```

## 📚 查看日志

```bash
# 后端日志
tail -f backend/backend.log

# 管理端日志
tail -f frontend/admin/admin.log

# Web 用户端日志
tail -f frontend/web/web.log
```

## ⚙️ 环境配置说明

**Java 21 配置:**
- 已添加到 `~/.zshrc` 和 `~/.zprofile`
- JDK 路径: `/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home`

**数据库配置:**
- MySQL 8.0: 用户名 `root`，无密码
- Redis: 无密码，端口 6379

**后端配置:**
- 端口: 19999 (查看 `backend/src/main/resources/application.yml`)
- 数据库连接已配置

**前端配置:**
- 管理端: `frontend/admin/src/axios.js` → `baseURL: 'http://127.0.0.1:19999'`
- Web 用户端: `frontend/web/.env.dev` → `VITE_API_BASE_PREFIX = 'http://127.0.0.1:19999'`

## 🐳 后续可选配置

### 1. 配置邮箱服务（可选）

编辑 `backend/src/main/resources/application.yml` 第 26-28 行:
```yaml
mail:
  username: your_qq_email # QQ 邮箱
  password: your_qq_email_password # QQ 邮箱授权码
```

### 2. 配置 MinIO 对象存储（可选）

编辑 `backend/src/main/resources/application.yml` 第 63-67 行:
```yaml
minio:
  access-key: your_minio_access_key
  secret-key: your_minio_secret_key
  url: your_url
```

### 3. 配置 Ollama AI 服务（可选）

编辑 `backend/src/main/resources/application.yml` 第 130-138 行:
```yaml
ai:
  ollama:
    url: http://127.0.0.1:11434
    chat:
      options:
        model: qwen2.5:7b
    embedding:
      options:
        model: nomic-embed-text
```

**注意**: 当前项目已成功构建，所有核心功能可用。AI 相关功能需要额外安装 Ollama。
