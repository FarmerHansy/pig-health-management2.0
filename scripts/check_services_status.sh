#!/bin/bash

echo "=================================================="
echo "📊 MySQL 和 Redis 服务状态检查"
echo "=================================================="
echo ""

# 方法 1: 使用 brew services list
echo "方法 1: 使用 brew 服务管理"
brew services list | grep -E "(mysql@8.0|redis)"
echo ""

# 方法 2: 检查端口
echo "方法 2: 检查端口监听"
echo "  MySQL 端口 3306:"
if lsof -i :3306 > /dev/null 2>&1; then
    echo "    ✅ MySQL 正在监听端口 3306"
    lsof -i :3306 | grep LISTEN | awk '{print "    进程:", $1, "PID:", $2}'
else
    echo "    ❌ MySQL 未监听端口 3306"
fi

echo "  Redis 端口 6379:"
if lsof -i :6379 > /dev/null 2>&1; then
    echo "    ✅ Redis 正在监听端口 6379"
    lsof -i :6379 | grep LISTEN | awk '{print "    进程:", $1, "PID:", $2}'
else
    echo "    ❌ Redis 未监听端口 6379"
fi
echo ""

# 方法 3: 直接连接测试
echo "方法 3: 直接连接测试"
echo "  MySQL 连接测试:"
if /opt/homebrew/opt/mysql@8.0/bin/mysql -u root -e "SELECT 1;" > /dev/null 2>&1; then
    echo "    ✅ MySQL 连接成功"
else
    echo "    ❌ MySQL 连接失败"
fi

echo "  Redis 连接测试:"
if redis-cli ping > /dev/null 2>&1; then
    echo "    ✅ Redis 连接成功 (PING: $(redis-cli ping))"
else
    echo "    ❌ Redis 连接失败"
fi
echo ""

# 方法 4: 查看进程
echo "方法 4: 查看进程"
echo "  MySQL 进程:"
ps aux | grep mysqld | grep -v grep | awk '{print "    PID:", $2, "用户:", $1, "命令:", $11}'

echo "  Redis 进程:"
ps aux | grep redis-server | grep -v grep | awk '{print "    PID:", $2, "用户:", $1, "命令:", $11}'
echo ""

echo "=================================================="
