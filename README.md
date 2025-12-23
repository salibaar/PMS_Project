# 🚩 Planning Management System (PMS) - Hệ Thống Quản Lý Kế Hoạch

## 📋 Tổng Quan

Hệ thống quản lý kế hoạch (PMS) là ứng dụng web full-stack giúp tổ chức lập và quản lý kế hoạch công việc, nhiệm vụ với khả năng đánh dấu các nhiệm vụ đột phá ưu tiên.

### 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│   React         │ ───> │  Spring Boot    │ ───> │  PostgreSQL     │
│   Frontend      │      │  Backend API    │      │  Database       │
│   Port: 3000    │      │  Port: 8080     │      │  Port: 5432     │
└─────────────────┘      └─────────────────┘      └─────────────────┘
```

### 🛠️ Công Nghệ Sử Dụng

**Backend:**
- ☕ Java 17
- 🍃 Spring Boot 2.7.17
- 🗃️ Spring Data JPA
- ✅ Spring Validation
- 📦 Lombok
- 🐘 PostgreSQL

**Frontend:**
- ⚛️ React 18.2.0
- 🎨 Inline Styling (CSS-in-JS)
- 🔄 Fetch API

**DevOps:**
- 🐳 Docker & Docker Compose
- 🏗️ Multi-stage Docker builds

---

## 🚀 Cài Đặt và Chạy

### Yêu Cầu Hệ Thống

- Docker Desktop (hoặc Docker Engine + Docker Compose)
- Git

### Bước 1: Clone Repository

```bash
git clone https://github.com/salibaar/PMS_Project.git
cd PMS_Project
```

### Bước 2: Cấu Hình Environment Variables

Tạo file `.env` từ template (hoặc sửa file `.env` có sẵn):

```bash
# Database Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=securepassword    # ⚠️ Đổi password trong production
POSTGRES_DB=mydatabase

# JWT Configuration
APP_JWT_SECRET=your-secret-key-min-256-bits-change-in-production  # ⚠️ Bắt buộc đổi trong production
```

**⚠️ LƯU Ý Bảo Mật:**
- **KHÔNG** commit file `.env` vào Git trong môi trường production
- Đổi `POSTGRES_PASSWORD` thành mật khẩu mạnh
- Đổi `APP_JWT_SECRET` thành chuỗi ngẫu nhiên ít nhất 256 bits

### Bước 3: Khởi Động Hệ Thống

```bash
docker-compose up --build
```

Lệnh này sẽ:
1. ✅ Tạo PostgreSQL database container
2. ✅ Build và chạy Spring Boot backend
3. ✅ Build và chạy React frontend

### Bước 4: Truy Cập Ứng Dụng

- **Frontend (Giao diện web):** http://localhost:3000 ← **TRUY CẬP ĐÂY**
- **Backend API:** http://localhost:8080 (REST API endpoints)
- **Database:** localhost:5432

**⚠️ Lưu ý:** 
- Để sử dụng ứng dụng, truy cập **Frontend** tại http://localhost:3000
- Backend (http://localhost:8080) chỉ là API server, truy cập sẽ thấy thông tin API

---

## 📡 API Documentation

### Root Endpoint

**GET** `/`

Trả về thông tin về API và danh sách endpoints có sẵn.

**Response:**
```json
{
  "application": "Planning Management System (PMS)",
  "version": "1.0.0",
  "status": "running",
  "endpoints": {
    "POST /api/v1/planning/objectives": "Tạo nhiệm vụ mới",
    "GET /api/v1/health": "Kiểm tra trạng thái hệ thống"
  },
  "frontend": {
    "url": "http://localhost:3000"
  }
}
```

### Health Check

**GET** `/api/v1/health`

Kiểm tra trạng thái backend API.

### Endpoint: Tạo Nhiệm Vụ Mới

**POST** `/api/v1/planning/objectives`

**Request Body:**
```json
{
  "planId": 2026,
  "content": "Chuyển đổi số toàn diện trong tổ chức",
  "isBreakthrough": true,
  "keyResults": ["KPI 1", "KPI 2"]
}
```

**Validation Rules:**
- `planId`: Bắt buộc (Integer)
- `content`: Bắt buộc, độ dài 10-500 ký tự
- `isBreakthrough`: Bắt buộc (Boolean)
- `keyResults`: Tùy chọn (Array of strings)

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Nhiệm vụ đã được lưu thành công",
  "data": {
    "planId": 2026,
    "content": "Chuyển đổi số toàn diện trong tổ chức",
    "isBreakthrough": true,
    "keyResults": ["KPI 1", "KPI 2"]
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "content": "Nội dung phải từ 10 đến 500 ký tự",
  "planId": "Plan ID không được để trống"
}
```

---

## 🔧 Development Setup (Không dùng Docker)

### Backend (Spring Boot)

```bash
cd backend

# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Hoặc chạy file JAR
java -jar target/pms-app-0.0.1-SNAPSHOT.jar
```

**Yêu cầu:**
- Java 17+
- Maven 3.6+
- PostgreSQL đang chạy trên port 5432

**Environment Variables (Backend):**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mydatabase
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=securepassword
export APP_JWT_SECRET=your-secret-key
```

### Frontend (React)

```bash
cd frontend

# Cài đặt dependencies
npm install

# Chạy development server
npm start

# Build for production
npm run build
```

**Environment Variables (Frontend):**

Tạo file `frontend/.env`:
```
REACT_APP_API_URL=http://localhost:8080/api/v1
```

---

## 📂 Cấu Trúc Thư Mục

```
PMS_Project/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/gov/pms/
│   │       │   ├── PmsApplication.java
│   │       │   └── PlanningController.java
│   │       └── resources/
│   │           └── application.properties
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── App.js
│   │   └── index.js
│   ├── public/
│   ├── Dockerfile
│   ├── package.json
│   └── .env.example
├── docker-compose.yml
├── .env
└── README.md
```

---

## 🧪 Testing

### Backend Tests

```bash
cd backend
mvn test
```

### Frontend Tests

```bash
cd frontend
npm test
```

---

## 🐛 Troubleshooting

### Lỗi: "Whitelabel Error Page" hoặc 404 Not Found

**Triệu chứng:** Thấy trang lỗi trắng với message "This application has no explicit mapping for /error"

**Nguyên nhân:** Đang truy cập backend API (http://localhost:8080) thay vì frontend

**Giải pháp:**
- Truy cập **Frontend** tại: http://localhost:3000
- Backend (http://localhost:8080) là REST API server, không có giao diện web
- Truy cập http://localhost:8080 sẽ thấy thông tin API (JSON response)

### Lỗi: "FATAL: role 'postgres' does not exist" hoặc "password authentication failed"

**Nguyên nhân:** Docker volume có dữ liệu cũ từ lần chạy trước.

**Giải pháp nhanh:**
```bash
bash fix-postgres-error.sh
```

**Hoặc thủ công:**
```bash
docker compose down -v
docker compose up --build
```

Xem chi tiết trong **HUONG_DAN_CHAY.md**

### Lỗi: "Port 5432 already in use"

PostgreSQL đang chạy trên máy. Giải pháp:
1. Dừng PostgreSQL local: `sudo service postgresql stop`
2. Hoặc đổi port trong `docker-compose.yml`: `"5433:5432"`

### Lỗi: "Connection refused" từ frontend

1. Kiểm tra backend đang chạy: `docker ps`
2. Kiểm tra logs: `docker logs spring_backend`
3. Đảm bảo CORS được cấu hình đúng

### Lỗi: "JWT secret not found"

Đảm bảo `APP_JWT_SECRET` được định nghĩa trong file `.env`

---

## 🔒 Bảo Mật

### Checklist Bảo Mật Trước Khi Deploy Production:

- [ ] Đổi `POSTGRES_PASSWORD` thành mật khẩu mạnh (>16 ký tự, có chữ hoa, số, ký tự đặc biệt)
- [ ] Đổi `APP_JWT_SECRET` thành chuỗi ngẫu nhiên ít nhất 256 bits
- [ ] Không commit file `.env` vào Git
- [ ] Cập nhật CORS origins trong `PlanningController.java` theo domain thật
- [ ] Bật HTTPS/TLS
- [ ] Giới hạn rate limiting cho API
- [ ] Thêm Spring Security với authentication/authorization
- [ ] Enable database encryption at rest
- [ ] Sử dụng secrets manager (AWS Secrets Manager, Azure Key Vault, etc.)

---

## 📝 License

Dự án nội bộ - Không public license

---

## 👥 Contributors

- Salibaar (Repository Owner)

---

## 📧 Support

Liên hệ: [GitHub Issues](https://github.com/salibaar/PMS_Project/issues)

---

**Phiên bản:** 0.0.1-SNAPSHOT  
**Cập nhật:** December 2024
