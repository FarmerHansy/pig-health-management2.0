#!/bin/bash

echo "=================================================="
echo "📊 服务状态检查"
echo "=================================================="
echo ""

# 检查环境
echo "✅ 已安装的环境:"
java -version 2>&1 | head -1
mvn -version 2>&1 | grep "Maven home"
mysql --version
redis-cli --version
node --version
npm --version
echo ""

# 检查数据库
echo "🗄️  数据库状态:"
mysql -u root -e "SELECT COUNT(*) as users FROM pig_health_smart_medicine.user;" 2>/dev/null || echo "  ❌ MySQL 未运行或连接失败"
echo ""

# 检查服务端口
echo "🔌 服务端口状态:"
if lsof -i :19999 > /dev/null 2>&1; then
    echo "  ✅ 后端 API (19999): 运行中"
    curl -s http://localhost:19999/doc.html | grep -q "<title>" && echo "     API 文档可用"
else
    echo "  ❌ 后端 API (19999): 未运行"
fi

if lsof -i :5173 > /dev/null 2>&1; then
    echo "  ✅ 管理端前端 (5173): 运行中"
    echo "     访问: http://localhost:5173"
else
    echo "  ❌ 管理端前端 (5173): 未运行"
fi

if lsof -i :5174 > /dev/null 2>&1; then
    echo "  ✅ Web 用户端 (5174): 运行中"
    echo "     访问: http://localhost:5174"
else
    echo "  ❌ Web 用户端 (5174): 未运行"
fi

echo ""
echo "=================================================="
