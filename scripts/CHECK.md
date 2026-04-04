# 快速检查 MySQL 和 Redis 服务

## 最简单的方法（推荐）

直接运行项目目录下的脚本：

```bash
cd /Users/aas/Desktop/PigHealthSmartMedicine
./check_db.sh
```

**输出示例:**
```
✅ MySQL 运行中 (3306)
✅ Redis 运行中 (6379)
```

## 其他检查方法

### 1. 使用 brew 命令

```bash
brew services list
```

查看输出中 `mysql@8.0` 和 `redis` 的状态：
- `started` - 服务正在运行
- `stopped` - 服务未运行

### 2. 检查端口监听

```bash
# MySQL 端口 3306
lsof -i :3306

# Redis 端口 6379
lsof -i :6379
```

如果看到进程在监听端口，说明服务正在运行。

### 3. 连接测试

```bash
# MySQL 连接测试
mysql -u root -e "SELECT 1;"

# Redis 连接测试
redis-cli ping
```

如果返回结果，说明服务正常运行。

### 4. 查看进程

```bash
# MySQL 进程
ps aux | grep mysqld

# Redis 进程
ps aux | grep redis-server
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

---

**当前状态:** ✅ 两个服务都在运行中
