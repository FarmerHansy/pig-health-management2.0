#!/bin/bash

# 设置 Java 21 为默认
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# 启动 MySQL 和 Redis
echo "🔄 检查 MySQL 和 Redis 服务..."
brew services start mysql@8.0 >/dev/null 2>&1
brew services start redis >/dev/null 2>&1

echo "✅ MySQL 和 Redis 已启动"

# 启动后端服务
echo "🚀 启动后端服务..."
cd /Users/aas/Desktop/PigHealthSmartMedicine/backend
nohup mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!
echo "✅ 后端服务已启动 (PID: $BACKEND_PID)"

sleep 8

# 检查后端是否启动成功
if lsof -i :19999 > /dev/null 2>&1; then
    echo "✅ 后端服务运行正常 (http://localhost:19999)"
else
    echo "❌ 后端服务启动失败"
    exit 1
fi

# 启动管理端前端
echo "🚀 启动管理端前端..."
cd /Users/aas/Desktop/PigHealthSmartMedicine/frontend/admin
nohup npm run dev > admin.log 2>&1 &
ADMIN_PID=$!
echo "✅ 管理端前端已启动 (PID: $ADMIN_PID)"

sleep 5

# 启动 Web 用户端前端
echo "🚀 启动 Web 用户端前端..."
cd /Users/aas/Desktop/PigHealthSmartMedicine/frontend/web
nohup npm run dev > web.log 2>&1 &
WEB_PID=$!
echo "✅ Web 用户端前端已启动 (PID: $WEB_PID)"

sleep 5

# 显示服务信息
echo ""
echo "=================================================="
echo "✅ 所有服务已启动!"
echo "=================================================="
echo "📱 后端 API:        http://localhost:19999"
echo "📱 API 文档:        http://localhost:19999/doc.html"
echo "📱 管理端前端:      http://localhost:5173"
echo "📱 Web 用户端前端:  http://localhost:5174"
echo ""
echo "📋 测试账号:"
echo "   管理员: admin / 123456"
echo "   普通用户: linyi / 123456"
echo ""
echo "💾 服务 PID:"
echo "   后端: $BACKEND_PID"
echo "   管理端: $ADMIN_PID"
echo "   Web: $WEB_PID"
echo ""
echo "🔧 日志文件:"
echo "   后端: /Users/aas/Desktop/PigHealthSmartMedicine/backend/backend.log"
echo "   管理端: /Users/aas/Desktop/PigHealthSmartMedicine/frontend/admin/admin.log"
echo "   Web: /Users/aas/Desktop/PigHealthSmartMedicine/frontend/web/web.log"
echo "=================================================="
