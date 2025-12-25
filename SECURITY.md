# 🔒 Security Policy

## Báo Cáo Lỗ Hổng Bảo Mật

Nếu bạn phát hiện lỗ hổng bảo mật trong dự án, vui lòng:
1. **KHÔNG** tạo public issue
2. Liên hệ trực tiếp với maintainer qua email hoặc private message
3. Cung cấp chi tiết về lỗ hổng và cách tái tạo

---

## ✅ Security Checklist cho Production

### 1. Environment Variables & Secrets

#### Hiện Tại (Development)
```bash
POSTGRES_PASSWORD=securepassword  # ❌ PHẢI ĐỔI
APP_JWT_SECRET=your-secret-key-min-256-bits-change-in-production  # ❌ PHẢI ĐỔI
```

#### Khuyến Nghị (Production)
```bash
# Sử dụng password mạnh
POSTGRES_PASSWORD=<random-strong-password>  # Ít nhất 20 ký tự, có chữ hoa, thường, số, ký tự đặc biệt

# Sử dụng JWT secret ngẫu nhiên
APP_JWT_SECRET=<random-256-bit-secret>  # Tạo bằng: openssl rand -base64 32
```

**Cách tạo JWT secret an toàn:**
```bash
openssl rand -base64 32
# Output: V7pQx9K8mNb2Lc4HfDjT5sWqRtYu3ZaE6iBvXnCgMoP=
```

### 2. Database Security

- [ ] Đổi default PostgreSQL credentials
- [ ] Giới hạn database connections từ trusted IPs only
- [ ] Enable SSL/TLS connections
- [ ] Regular backups với encryption
- [ ] Áp dụng principle of least privilege cho database users
- [ ] Enable PostgreSQL audit logging

**Cấu hình SSL cho PostgreSQL:**
```yaml
# docker-compose.yml
db:
  command: >
    postgres
    -c ssl=on
    -c ssl_cert_file=/etc/ssl/certs/server.crt
    -c ssl_key_file=/etc/ssl/private/server.key
```

### 3. API Security

#### CORS Configuration

**Hiện tại:**
```java
@CrossOrigin(origins = "http://localhost:3000")  // ❌ Chỉ cho development
```

**Khuyến nghị:**
```java
@CrossOrigin(
    origins = "${app.cors.allowed-origins}",  // Đọc từ config
    allowedMethods = {"GET", "POST", "PUT", "DELETE"},
    allowCredentials = "true",
    maxAge = 3600
)
```

**application.properties:**
```properties
app.cors.allowed-origins=https://yourdomain.com,https://www.yourdomain.com
```

#### Input Validation

✅ **Đã có:** Validation constraints trong `ObjectiveRequest`
```java
@NotBlank(message = "Nội dung nhiệm vụ không được để trống")
@Size(min = 10, max = 500, message = "Nội dung phải từ 10 đến 500 ký tự")
private String content;
```

❌ **Thiếu:**
- SQL Injection protection (JPA tự động xử lý, nhưng cần cẩn thận với native queries)
- XSS protection (sanitize HTML input)
- CSRF protection

### 4. Add Spring Security (Khuyến nghị)

**Thêm dependency vào `pom.xml`:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

### 5. HTTPS/TLS

**Production deployment MUST use HTTPS.**

**Với Docker + Nginx reverse proxy:**
```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    
    location / {
        proxy_pass http://react_frontend:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-Proto https;
    }
    
    location /api {
        proxy_pass http://spring_backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-Proto https;
    }
}
```

### 6. Logging & Monitoring

✅ **Đã có:** SLF4J logger
```java
private static final Logger logger = LoggerFactory.getLogger(PlanningController.class);
```

**Khuyến nghị thêm:**
- Log all authentication attempts
- Log all authorization failures
- Monitor suspicious activities
- Set up alerts for security events

### 7. Dependency Scanning

**Chạy security scan trước deploy:**
```bash
# Maven security check
mvn org.owasp:dependency-check-maven:check

# npm audit
cd frontend && npm audit

# Hoặc dùng Snyk, Dependabot, etc.
```

### 8. Docker Security

**Best practices:**
```dockerfile
# Backend Dockerfile - Improvements
FROM eclipse-temurin:17-jdk-alpine

# Tạo non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Giới hạn resources
ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 9. Rate Limiting

**Thêm vào Spring Boot để chống DDoS:**
```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>
```

### 10. Environment-specific Configuration

**application-prod.properties:**
```properties
# Production settings
spring.jpa.hibernate.ddl-auto=validate  # ❌ KHÔNG dùng "update" trong production
spring.jpa.show-sql=false  # ❌ Tắt SQL logging
server.error.include-stacktrace=never  # ❌ Không show stacktrace cho client
logging.level.root=WARN
logging.level.com.gov.pms=INFO
```

---

## 🚨 Các Lỗ Hổng Đã Biết & Fixes

### 1. Missing JWT Implementation
**Status:** Acknowledged  
**Severity:** High  
**Description:** JWT secret được config nhưng chưa có implementation  
**Fix:** Thêm Spring Security + JWT authentication filter

### 2. Weak Default Credentials
**Status:** Documented  
**Severity:** Critical  
**Description:** Default password trong `.env` là "securepassword"  
**Fix:** Đã thêm warning trong README và SECURITY.md

### 3. Permissive CORS
**Status:** Development only  
**Severity:** Medium  
**Description:** CORS cho phép localhost:3000  
**Fix:** Phải cấu hình lại cho production domain

---

## 📚 Security Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Docker Security Best Practices](https://docs.docker.com/develop/security-best-practices/)
- [PostgreSQL Security](https://www.postgresql.org/docs/current/security.html)

---

## 🔄 Regular Security Maintenance

- [ ] Update dependencies monthly
- [ ] Run security scans weekly
- [ ] Review logs daily
- [ ] Rotate secrets quarterly
- [ ] Conduct penetration testing annually

---

**Cập nhật:** December 2024
