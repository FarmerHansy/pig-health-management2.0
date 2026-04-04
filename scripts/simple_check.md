# MySQL 和 Redis 服务检查（极简版）

## 一键检查

```bash
./simple_check.sh
```

或者直接执行：

```bash
lsof -i :3306 && echo "✅ MySQL 运行中" || echo "❌ MySQL 未运行"
lsof -i :6379 && echo "✅ Redis 运行中" || echo "❌ Redis 未运行"
```

## 查看所有 brew 服务

```bash
brew services list
```

**预期输出:**
```
Name         Status  User File
mysql@8.0    started aas  ...
redis        started aas  ...
```

## 检查端口

```bash
# 查看哪个进程占用了端口
lsof -i :3306  # MySQL
lsof -i :6379  # Redis
```

## 连接测试

```bash
# MySQL 测试
mysql -u root -e "SELECT 1;"

# Redis 测试
redis-cli ping
```

## 启动/停止

```bash
# 启动
brew services start mysql@8.0
brew services start redis

# 停止
brew services stop mysql@8.0
brew services stop redis
```
