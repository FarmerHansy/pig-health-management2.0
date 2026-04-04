# 项目服务启动完成 ✅

## 环境安装

| 组件 | 版本 | 状态 |
|------|------|------|
| Java JDK | 21.0.10 | ✅ 已安装 |
| Maven | 3.9.12 | ✅ 已安装 |
| MySQL | 8.0.45 | ✅ 已安装 |
| Redis | 8.6.0 | ✅ 已安装 |
| Node.js | 24.10.0 | ✅ 已安装 |
| npm | 11.6.1 | ✅ 已安装 |

## 服务状态

| 服务 | 状态 | 端口 | 访问地址 |
|------|------|------|----------|
| 后端 API | ✅ 运行中 | 19999 | [http://localhost:19999](http://localhost:19999) |
| API 文档 | ✅ 可用 | 19999 | [http://localhost:19999/doc.html](http://localhost:19999/doc.html) |
| 管理端前端 | ✅ 运行中 | 5173 | [http://localhost:5173](http://localhost:5173) |
| Web 用户端 | ✅ 运行中 | 5174 | [http://localhost:5174](http://localhost:5174) |
| MySQL | ✅ 运行中 | 3306 | - |
| Redis | ✅ 运行中 | 6379 | - |

## 测试账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | 123456 | 管理员 |
| linyi | 123456 | 普通用户 |

## 快速命令

```bash
# 检查服务状态
./status_services.sh

# 停止所有服务
cd /Users/aas/Desktop/PigHealthSmartMedicine && ./stop_services.sh

# 启动所有服务
cd /Users/aas/Desktop/PigHealthSmartMedicine && ./start_services.sh

# 重启所有服务
./stop_services.sh && ./start_services.sh
```

## 配置文件位置

- **后端配置**: `backend/src/main/resources/application.yml`
- **管理端前端配置**: `frontend/admin/src/axios.js`
- **Web 用户端配置**: `frontend/web/.env.dev`

## 环境变量配置

Java 21 和 MySQL 已配置到:
- `~/.zshrc`
- `~/.zprofile`

## 下一步

1. 访问管理端: [http://localhost:5173](http://localhost:5173)
2. 使用账号 `admin` / `123456` 登录
3. 访问 Web 用户端: [http://localhost:5174](http://localhost:5174)
4. 使用账号 `linyi` / `123456` 测试

## 可选配置

如需使用完整功能，可以配置:
- 邮箱服务 (用于用户注册验证)
- MinIO 对象存储 (用于文件上传)
- Ollama AI 服务 (用于智能问诊)
- 查看 `quick_start.md` 获取详细配置说明

---

项目已成功安装并启动！🎉
