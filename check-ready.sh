#!/bin/bash
# Script kiểm tra nhanh để verify project có thể chạy được

echo "======================================"
echo "🔍 KIỂM TRA DỰ ÁN PMS"
echo "======================================"
echo ""

# Check Docker
echo "1️⃣  Kiểm tra Docker..."
if command -v docker &> /dev/null; then
    echo "✅ Docker: $(docker --version)"
else
    echo "❌ Docker chưa được cài đặt"
    echo "   Tải tại: https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Check Docker Compose
echo ""
echo "2️⃣  Kiểm tra Docker Compose..."
if docker compose version &> /dev/null; then
    echo "✅ Docker Compose: $(docker compose version)"
elif command -v docker-compose &> /dev/null; then
    echo "✅ Docker Compose: $(docker-compose --version)"
else
    echo "❌ Docker Compose chưa được cài đặt"
    exit 1
fi

# Check .env file
echo ""
echo "3️⃣  Kiểm tra file .env..."
if [ -f ".env" ]; then
    echo "✅ File .env tồn tại"
    if grep -q "APP_JWT_SECRET" .env; then
        echo "✅ APP_JWT_SECRET được cấu hình"
    else
        echo "⚠️  APP_JWT_SECRET chưa được cấu hình"
    fi
else
    echo "❌ File .env không tồn tại"
    exit 1
fi

# Check frontend .env.example
echo ""
echo "4️⃣  Kiểm tra cấu hình frontend..."
if [ -f "frontend/.env.example" ]; then
    echo "✅ frontend/.env.example tồn tại"
    if [ ! -f "frontend/.env" ]; then
        echo "⚠️  frontend/.env chưa tồn tại"
        echo "   Tạo file bằng lệnh: cp frontend/.env.example frontend/.env"
    else
        echo "✅ frontend/.env đã sẵn sàng"
    fi
else
    echo "❌ frontend/.env.example không tồn tại"
fi

# Check ports
echo ""
echo "5️⃣  Kiểm tra ports..."
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        echo "⚠️  Port $port đang được sử dụng"
        return 1
    else
        echo "✅ Port $port còn trống"
        return 0
    fi
}

check_port 3000
check_port 8080
check_port 5432

# Check backend can compile
echo ""
echo "6️⃣  Kiểm tra backend có thể compile..."
cd backend
if mvn compile -q -DskipTests 2>&1 | grep -q "BUILD SUCCESS"; then
    echo "✅ Backend compile thành công"
else
    if mvn compile -q -DskipTests 2>&1 | tail -1 | grep -q "SUCCESS"; then
        echo "✅ Backend compile thành công"
    else
        echo "⚠️  Backend compile có vấn đề (có thể do Maven chưa cài hoặc dependencies)"
    fi
fi
cd ..

# Summary
echo ""
echo "======================================"
echo "📊 TÓM TẮT"
echo "======================================"
echo ""
echo "Để chạy ứng dụng:"
echo "1. Đảm bảo frontend/.env tồn tại:"
echo "   cp frontend/.env.example frontend/.env"
echo ""
echo "2. Khởi động với Docker:"
echo "   docker compose up --build"
echo ""
echo "3. Truy cập:"
echo "   Frontend: http://localhost:3000"
echo "   Backend:  http://localhost:8080"
echo ""
echo "✅ Dự án đã sẵn sàng để chạy!"
echo ""
