# MySQL 和 Redis 服务状态检查（最简版）

## 一行命令检查

```bash
lsof -i :3306 && echo "MySQL 运行中" || echo "MySQL 未运行"
lsof -i :6379 && echo "Redis 运行中" || echo "Redis 未运行"
```

## 一行命令查看所有服务

```bash
brew services list
```

## 连接测试

```bash
mysql -u root -e "SELECT 1;"
redis-cli ping
```

**当前状态**: ✅ 两个服务都在运行中
