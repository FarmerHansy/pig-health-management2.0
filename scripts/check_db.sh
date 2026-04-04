#!/bin/bash

# 一行命令检查数据库状态
lsof -i :3306 >/dev/null 2>&1 && echo "✅ MySQL 运行中 (3306)" || echo "❌ MySQL 未运行"
lsof -i :6379 >/dev/null 2>&1 && echo "✅ Redis 运行中 (6379)" || echo "❌ Redis 未运行"
