# 快速检查 MySQL 和 Redis 服务状态

## 简单命令

### 1. 查看所有服务状态（最简单）
```bash
brew services list
```

**输出说明:**
- `started` - 服务正在运行
- `stopped` - 服务未运行

### 2. 检查特定服务

#### MySQL
```bash
# 方法 1: 查看 brew 服务状态
brew services list | grep mysql

# 方法 2: 检查端口 3306
lsof -i :3306

# 方法 3: 直接连接测试
mysql -u root -e "SELECT 1;"

# 方法 4: 查看进程
ps aux | grep mysqld
```

#### Redis
```bash
# 方法 1: 查看 brew 服务状态
brew services list | grep redis

# 方法 2: 检查端口 6379
lsof -i :6379

# 方法 3: 直接连接测试
redis-cli ping

# 方法 4: 查看进程
ps aux | grep redis-server
```

## 常用快捷命令

```bash
# 一键检查两个服务
lsof -i :3306,:6379

# 检查并显示进程名和 PID
lsof -i :3306 -i :6379 | grep LISTEN

# 快速测试连接
mysql -u root -e "SELECT 1;" && redis-cli ping
```

## 启动/停止服务

```bash
# 启动服务
brew services start mysql@8.0
brew services start redis

# 停止服务
brew services stop mysql@8.0
brew services stop redis

# 重启服务
brew services restart mysql@8.0
brew services restart redis
```

## 使用专用脚本

```bash
# 运行详细检查脚本
./check_services_status.sh
```

## 预期输出

### 服务运行正常
```
mysql@8.0 started ...
redis     started ...
```

### MySQL 端口监听
```
mysqld  44136  aas  30u  IPv6 ... TCP *:3306 (LISTEN)
```

### Redis 连接测试
```
PONG
```

### MySQL 连接测试
```
1
1
```

---

**当前状态:**
- ✅ MySQL 8.0 - 运行中 (PID: 44136)
- ✅ Redis 8.6.0 - 运行中 (PID: 44110)
