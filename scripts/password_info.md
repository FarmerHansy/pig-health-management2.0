# MySQL 和 Redis 密码配置

## MySQL 8.0

- **用户名**: `root`
- **密码**: **无密码**（空）
- **主机**: `localhost`
- **端口**: 3306
- **数据库**: `pig_health_smart_medicine`

### 配置文件位置
`backend/src/main/resources/application.yml` 第 11-12 行:

```yaml
username: root               #数据库用户名
password:                    #数据库密码（空）
```

### 测试连接
```bash
mysql -u root
# 或
/opt/homebrew/opt/mysql@8.0/bin/mysql -u root
```

## Redis 8.6.0

- **密码**: **无密码**（空）
- **主机**: `127.0.0.1`
- **端口**: 6379
- **数据库**: 2

### 配置文件位置
`backend/src/main/resources/application.yml` 第 49 行:

```yaml
password:        # Redis密码（空）
```

### 测试连接
```bash
redis-cli ping
# 应返回: PONG
```

---

## 说明

在安装 MySQL 时，Homebrew 默认创建了一个**无密码**的 root 用户，仅允许从 localhost 访问。这在本地开发环境中是安全的。

如需设置密码，可以执行:

```bash
mysql_secure_installation
```
