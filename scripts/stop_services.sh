#!/bin/bash

echo "🛑 正在停止所有服务..."

# 停止后端
echo "  停止后端服务..."
pkill -f "mvn spring-boot:run" 2>/dev/null
sleep 2

# 停止前端
echo "  停止前端服务..."
pkill -f "vite" 2>/dev/null

# 检查是否还有进程在运行
sleep 2
if lsof -i :19999 > /dev/null 2>&1; then
    echo "  后端服务仍在运行 (端口 19999)"
    lsof -i :19999 | grep LISTEN
else
    echo "✅ 后端服务已停止"
fi

if lsof -i :5173 > /dev/null 2>&1; then
    echo "  管理端前端仍在运行 (端口 5173)"
    lsof -i :5173 | grep LISTEN
else
    echo "✅ 管理端前端已停止"
fi

if lsof -i :5174 > /dev/null 2>&1; then
    echo "  Web 用户端前端仍在运行 (端口 5174)"
    lsof -i :5174 | grep LISTEN
else
    echo "✅ Web 用户端前端已停止"
fi

echo ""
echo "✅ 所有服务已停止"
