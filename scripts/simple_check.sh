#!/bin/bash

# 简单检查脚本
echo "MySQL 端口 3306:" && lsof -i :3306 | grep LISTEN && echo "✅ MySQL 运行中" || echo "❌ MySQL 未运行"
echo ""
echo "Redis 端口 6379:" && lsof -i :6379 | grep LISTEN && echo "✅ Redis 运行中" || echo "❌ Redis 未运行"
