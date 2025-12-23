# 🚀 HƯỚNG DẪN CHẠY ỨNG DỤNG - Quick Start Guide

## ✅ Phần Mềm ĐÃ SẴN SÀNG để chạy!

Dự án đã được kiểm tra và cải thiện. Bạn có thể chạy ngay bây giờ theo các bước dưới đây.

---

## 📋 Phương Pháp 1: Chạy với Docker (KHUYẾN NGHỊ)

### Bước 1: Kiểm tra Docker đã cài đặt chưa

```bash
docker --version
docker compose version
```

Nếu chưa có Docker, tải tại: https://www.docker.com/products/docker-desktop

### Bước 2: Di chuyển vào thư mục dự án

```bash
cd PMS_Project
```

### Bước 3: Tạo file .env cho frontend (nếu chưa có)

```bash
cp frontend/.env.example frontend/.env
```

Hoặc tạo file `frontend/.env` với nội dung:
```
REACT_APP_API_URL=http://localhost:8080/api/v1
```

### Bước 4: Khởi động toàn bộ hệ thống

```bash
docker compose up --build
```

**Lệnh này sẽ:**
1. ✅ Tạo PostgreSQL database (port 5432)
2. ✅ Build và chạy Spring Boot backend (port 8080)
3. ✅ Build và chạy React frontend (port 3000)

### Bước 5: Truy cập ứng dụng

Sau khi các container khởi động xong (khoảng 2-3 phút), mở trình duyệt:

- **🌐 Giao diện web:** http://localhost:3000
- **📡 Backend API:** http://localhost:8080/api/v1/planning/objectives
- **🗄️ Database:** localhost:5432 (username: postgres, password: securepassword)

### Bước 6: Test chức năng

1. Mở http://localhost:3000 trên trình duyệt
2. Nhập nội dung nhiệm vụ (ít nhất 10 ký tự)
3. Chọn có phải nhiệm vụ đột phá hay không
4. Click "LƯU NHIỆM VỤ"
5. Nếu thành công, sẽ hiện thông báo "✅ LƯU THÀNH CÔNG!"

### Dừng ứng dụng

```bash
# Nhấn Ctrl+C trong terminal đang chạy docker compose
# Hoặc chạy lệnh:
docker compose down
```

---

## 📋 Phương Pháp 2: Chạy Không Dùng Docker (Development)

### Yêu Cầu:
- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.6+

### A. Chạy Backend

```bash
cd backend

# Set environment variables
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mydatabase
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=securepassword
export APP_JWT_SECRET=XgpwoMzjydGoMT9dXuMuRQoK1dZvUouX2Cqfs5Hyz6o=

# Compile và chạy
mvn spring-boot:run
```

Backend sẽ chạy tại: http://localhost:8080

### B. Chạy Frontend

Mở terminal mới:

```bash
cd frontend

# Tạo file .env nếu chưa có
echo "REACT_APP_API_URL=http://localhost:8080/api/v1" > .env

# Cài đặt dependencies
npm install

# Chạy development server
npm start
```

Frontend sẽ chạy tại: http://localhost:3000

---

## 🔍 Kiểm Tra Hệ Thống Đang Chạy

### Kiểm tra Backend:
```bash
# Kiểm tra trang chủ backend (trả về thông tin API)
curl http://localhost:8080

# Hoặc mở trình duyệt: http://localhost:8080
# Sẽ thấy thông tin về API và các endpoints có sẵn

# Kiểm tra health check
curl http://localhost:8080/api/v1/health
```

**Lưu ý quan trọng:** 
- Backend (http://localhost:8080) là REST API, không có giao diện web
- Để dùng ứng dụng, truy cập Frontend tại http://localhost:3000

### Kiểm tra Frontend:
Mở trình duyệt: http://localhost:3000
- Sẽ thấy giao diện "🚩 Lập Kế Hoạch 2026"

### Kiểm tra Database (nếu dùng Docker):
```bash
docker exec -it postgres_db psql -U postgres -d mydatabase -c "\dt"
```

---

## ❌ Xử Lý Lỗi Thường Gặp

### Lỗi: "Whitelabel Error Page" hoặc 404 khi truy cập http://localhost:8080
**Nguyên nhân:** Bạn đang truy cập backend API trực tiếp

**Giải pháp:**
1. **Để sử dụng ứng dụng:** Truy cập frontend tại http://localhost:3000
2. **Để xem thông tin API:** Truy cập http://localhost:8080 (trang chủ backend)
3. **Để test API:** Dùng Postman hoặc curl:
   ```bash
   curl -X POST http://localhost:8080/api/v1/planning/objectives \
     -H "Content-Type: application/json" \
     -d '{"planId":2026,"content":"Test nhiệm vụ mới","isBreakthrough":true,"keyResults":[]}'
   ```

**Giải thích:**
- Backend (port 8080) là REST API server, không có giao diện web
- Frontend (port 3000) là giao diện web React mà người dùng tương tác

---

## ❌ Xử Lý Lỗi Thường Gặp

### Lỗi: "Port 5432 already in use"
**Nguyên nhân:** PostgreSQL đã chạy trên máy

**Giải pháp:**
```bash
# Trên Linux/Mac:
sudo service postgresql stop

# Hoặc đổi port trong docker-compose.yml:
ports:
  - "5433:5432"  # Dùng port 5433 thay vì 5432
```

### Lỗi: "Port 8080 already in use"
**Nguyên nhân:** Một ứng dụng khác đang dùng port 8080

**Giải pháp:**
```bash
# Tìm process đang dùng port
lsof -i :8080
# Hoặc:
netstat -anp | grep 8080

# Kill process
kill -9 <PID>
```

### Lỗi: "Port 3000 already in use"
**Nguyên nhân:** Một ứng dụng khác đang dùng port 3000

**Giải pháp:**
```bash
# Tìm và kill process
lsof -i :3000
kill -9 <PID>
```

### Lỗi: "Connection refused" từ frontend
**Nguyên nhân:** Backend chưa sẵn sàng

**Giải pháp:**
1. Kiểm tra backend đang chạy: `curl http://localhost:8080`
2. Xem logs: `docker logs spring_backend`
3. Đợi thêm 1-2 phút cho backend khởi động

### Lỗi: "Failed to load resource: net::ERR_CONNECTION_REFUSED"
**Nguyên nhân:** Sai URL trong frontend/.env

**Giải pháp:**
Kiểm tra file `frontend/.env`:
```
REACT_APP_API_URL=http://localhost:8080/api/v1
```

---

## 📊 Logs và Debugging

### Xem logs của từng service:

```bash
# Database logs
docker logs postgres_db

# Backend logs
docker logs spring_backend

# Frontend logs
docker logs react_frontend

# Theo dõi logs realtime
docker logs -f spring_backend
```

---

## ✅ Checklist Khởi Động

- [ ] Docker/Docker Compose đã cài đặt
- [ ] File `.env` có đầy đủ thông tin (ở thư mục gốc)
- [ ] File `frontend/.env` đã được tạo
- [ ] Port 3000, 8080, 5432 không bị chiếm
- [ ] Đã chạy `docker compose up --build`
- [ ] Đợi 2-3 phút cho các service khởi động
- [ ] Truy cập http://localhost:3000 để test

---

## 🎉 Kết Luận

**Phần mềm ĐÃ SẴN SÀNG để chạy!**

Sau khi làm theo các bước trên, bạn có thể:
1. ✅ Truy cập giao diện web tại http://localhost:3000
2. ✅ Tạo và lưu nhiệm vụ mới
3. ✅ Đánh dấu nhiệm vụ đột phá
4. ✅ Dữ liệu được lưu vào PostgreSQL database

Nếu gặp vấn đề, xem phần "Xử Lý Lỗi Thường Gặp" ở trên hoặc kiểm tra logs của từng service.

---

**Cập nhật:** 20/12/2024  
**Version:** 1.0 - Ready to Run
