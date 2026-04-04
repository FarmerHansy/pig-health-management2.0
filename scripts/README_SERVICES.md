# 项目服务管理指南

## 已安装的环境

- ✅ Java JDK 21.0.10 (Homebrew)
- ✅ Maven 3.9.12
- ✅ MySQL 8.0.45
- ✅ Redis 8.6.0
- ✅ Node.js 24.10.0
- ✅ npm 11.6.1

## 数据库

- 数据库名: `pig_health_smart_medicine`
- MySQL 用户: `root` (无密码)
- Redis: 无密码，端口 6379

## 启动服务

### 方法 1: 使用启动脚本（推荐）

```bash
cd /Users/aas/Desktop/PigHealthSmartMedicine
./start_services.sh
```

### 方法 2: 手动启动

1. **启动数据库服务**

```bash
brew services start mysql@8.0
brew services start redis
```

2. **启动后端服务**

```bash
cd /Users/aas/Desktop/PigHealthSmartMedicine/backend
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
mvn spring-boot:run
```

3. **启动管理端前端** (新终端)

```bash
cd /Users/aas/Desktop/PigHealthSmartMedicine/frontend/admin
npm run dev
```

4. **启动 Web 用户端前端** (新终端)

```bash
cd /Users/aas/Desktop/PigHealthSmartMedicine/frontend/web
npm run dev
```

## 停止服务

### 方法 1: 使用停止脚本（推荐）

```bash
cd /Users/aas/Desktop/PigHealthSmartMedicine
./stop_services.sh
```

### 方法 2: 手动停止

```bash
# 停止后端
pkill -f "mvn spring-boot:run"

# 停止前端
pkill -f "vite"

# 停止数据库服务
brew services stop mysql@8.0
brew services stop redis
```

## 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 后端 API | http://localhost:19999 | Spring Boot 后端服务 |
| API 文档 | http://localhost:19999/doc.html | Knife4j API 文档 |
| 管理端前端 | http://localhost:5173 | Vue 3 管理后台 |
| Web 用户端 | http://localhost:5174 | Vue 3 用户端 |

## 测试账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | 123456 | 管理员 |
| linyi | 123456 | 普通用户 |

## 查看日志

```bash
# 后端日志
tail -f /Users/aas/Desktop/PigHealthSmartMedicine/backend/backend.log

# 管理端日志
tail -f /Users/aas/Desktop/PigHealthSmartMedicine/frontend/admin/admin.log

# Web 用户端日志
tail -f /Users/aas/Desktop/PigHealthSmartMedicine/frontend/web/web.log
```

## 问题排查

### 检查服务状态

```bash
# 检查端口占用
lsof -i :19999
lsof -i :5173
lsof -i :5174

# 检查数据库连接
mysql -u root -e "SHOW DATABASES;"
redis-cli ping
```

### 后端启动失败

检查配置文件：
```
backend/src/main/resources/application.yml
```

确保数据库配置正确：
- MySQL: `username: root`, `password: ` (空)
- Redis: `password: ` (空)

### 前端连接失败

检查前端 API 配置：
- 管理端: `frontend/admin/src/axios.js` (baseURL)
- Web 用户端: `frontend/web/.env.dev` (VITE_API_BASE_PREFIX)

### 重新安装依赖

```bash
# 重新安装后端依赖
cd backend
mvn clean install -DskipTests

# 重新安装前端依赖
cd frontend/admin
npm install

cd ../web
npm install
```

## 服务启动顺序

1. ✅ 环境依赖已安装 (Java, Maven, MySQL, Redis, Node.js)
2. ✅ 后端已构建 (jar 包已生成)
3. ✅ 数据库已初始化
4. ✅ 前端依赖已安装

## 注意事项

- 所有服务必须在项目目录下启动
- Java 环境已配置在 `~/.zshrc` 中
- 后端 API 端口已从默认的 9999 改为 19999 (查看 `application.yml`)
- 前端 API 请求地址已更新为 `http://127.0.0.1:19999`
