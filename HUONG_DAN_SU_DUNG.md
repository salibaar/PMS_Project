# 📖 Hướng Dẫn Sử Dụng - Planning Management System

## 🎯 Giới Thiệu

Hệ thống Quản Lý Kế Hoạch (PMS) giúp bạn lập và quản lý các nhiệm vụ kế hoạch cho năm 2026. Ứng dụng cho phép phân loại nhiệm vụ thành hai loại:
- **Nhiệm vụ đột phá**: Những mục tiêu quan trọng, ưu tiên cao
- **Nhiệm vụ thường xuyên**: Các công việc định kỳ, thường xuyên

---

## 🚀 Cách Sử Dụng

### Bước 1: Truy Cập Ứng Dụng

Mở trình duyệt web và truy cập: **http://localhost:3000**

Bạn sẽ thấy giao diện với tiêu đề "🚩 Lập Kế Hoạch 2026"

### Bước 2: Nhập Nội Dung Nhiệm Vụ

1. Tìm ô văn bản có nhãn **"Nhiệm vụ:"**
2. Click vào ô văn bản
3. Gõ mô tả nhiệm vụ của bạn

**Yêu cầu:**
- Tối thiểu: 10 ký tự
- Tối đa: 500 ký tự

**Ví dụ nhiệm vụ tốt:**
```
✅ "Chuyển đổi số toàn diện trong hoạt động quản lý và điều hành"
✅ "Nâng cao chất lượng dịch vụ công trực tuyến"
✅ "Xây dựng hệ thống báo cáo tự động"
```

**Ví dụ nhiệm vụ không hợp lệ:**
```
❌ "Test" (quá ngắn - chỉ 4 ký tự)
❌ "OK" (quá ngắn - chỉ 2 ký tự)
```

### Bước 3: Chọn Loại Nhiệm Vụ

Dưới ô nhập liệu, bạn sẽ thấy **nút gạt** (toggle switch):

#### Nhiệm Vụ Thường Xuyên (Màu Xám)
- Nút gạt ở vị trí bên trái
- Văn bản hiển thị: "📋 Nhiệm vụ thường xuyên"
- Dùng cho các công việc định kỳ, không quá khẩn cấp

#### Nhiệm Vụ Đột Phá (Màu Cam)
- Click vào nút gạt để chuyển sang màu cam
- Nút gạt di chuyển sang bên phải
- Văn bản hiển thị: "🔥 NHIỆM VỤ ĐỘT PHÁ (Ưu tiên cao)"
- Dùng cho các mục tiêu quan trọng, cần ưu tiên

**Cách chuyển đổi:**
- Click vào nút gạt hoặc vùng xung quanh nó
- Nút sẽ tự động chuyển trạng thái

### Bước 4: Lưu Nhiệm Vụ

1. Kiểm tra đã nhập đủ 10 ký tự (có số đếm ký tự bên dưới ô nhập)
2. Click nút **"💾 LƯU NHIỆM VỤ"** màu xanh lá

**Kết quả:**
- ✅ Nếu thành công: Popup "LƯU THÀNH CÔNG! Hệ thống đã ghi nhận"
- ❌ Nếu thất bại: Popup thông báo lỗi

### Bước 5: Tiếp Tục Thêm Nhiệm Vụ

Sau khi lưu thành công:
- Ô văn bản sẽ tự động xóa sạch
- Bạn có thể tiếp tục nhập nhiệm vụ mới

---

## 💡 Các Tính Năng

### 1. Nút "❓ Hướng dẫn"
- Ở góc trên bên phải màn hình
- Click để xem/ẩn hướng dẫn sử dụng ngắn gọn
- Click "✕ Đóng" để ẩn hướng dẫn

### 2. Đếm Ký Tự
- Hiển thị số ký tự đã nhập
- Cảnh báo nếu chưa đủ 10 ký tự
- Dấu ✅ khi đã đủ yêu cầu

### 3. Nút Lưu Thông Minh
- Màu xanh lá khi có thể lưu (≥10 ký tự)
- Màu xám khi chưa thể lưu (<10 ký tự)
- Hiển thị thông báo rõ ràng

### 4. Thanh Trạng Thái
- Ở cuối form
- Hiển thị thông tin backend và database
- Giúp xác nhận hệ thống đang hoạt động

---

## ❓ Câu Hỏi Thường Gặp

### Q1: Tôi không thấy gì ngoài các chữ, làm sao sử dụng?

**Trả lời:** Bạn đã mở đúng trang! Đây là cách sử dụng:
1. Click vào ô trống bên dưới chữ "Nhiệm vụ:"
2. Gõ nội dung (ít nhất 10 ký tự)
3. Click vào nút gạt nếu muốn đánh dấu "Đột phá"
4. Click nút "LƯU NHIỆM VỤ" màu xanh

### Q2: Tôi click "LƯU NHIỆM VỤ" nhưng không có gì xảy ra?

**Nguyên nhân:** Chưa nhập đủ 10 ký tự

**Giải pháp:** 
- Kiểm tra số ký tự bên dưới ô nhập
- Nhập thêm text cho đến khi thấy dấu ✅
- Nút "LƯU NHIỆM VỤ" sẽ chuyển từ màu xám sang xanh lá

### Q3: Nút gạt để làm gì?

**Trả lời:** Nút gạt giúp phân loại nhiệm vụ:
- **Trái (xám)**: Nhiệm vụ thường xuyên
- **Phải (cam)**: Nhiệm vụ đột phá (ưu tiên cao)

Click vào để chuyển đổi giữa hai trạng thái.

### Q4: Dữ liệu được lưu ở đâu?

**Trả lời:** 
- Dữ liệu được gửi tới backend API (http://localhost:8080)
- Backend lưu vào PostgreSQL database
- Xem thanh trạng thái ở cuối form để biết thêm chi tiết

### Q5: Làm sao biết nhiệm vụ đã lưu thành công?

**Trả lời:** Bạn sẽ thấy:
- Popup thông báo "✅ LƯU THÀNH CÔNG!"
- Ô văn bản tự động xóa sạch
- Sẵn sàng nhập nhiệm vụ tiếp theo

### Q6: Tôi có thể xem lại các nhiệm vụ đã lưu không?

**Trả lời:** Hiện tại ứng dụng chỉ hỗ trợ tạo nhiệm vụ mới. Chức năng xem danh sách nhiệm vụ sẽ được thêm vào trong phiên bản sau.

---

## 🎨 Giao Diện

```
┌─────────────────────────────────────────┐
│  🚩 Lập Kế Hoạch 2026    [❓ Hướng dẫn] │
├─────────────────────────────────────────┤
│                                         │
│  Nhiệm vụ: (Tối thiểu 10 ký tự)       │
│  ┌─────────────────────────────────┐   │
│  │ Nhập mô tả nhiệm vụ ở đây...   │   │
│  │                                 │   │
│  └─────────────────────────────────┘   │
│  0 ký tự (cần thêm 10)                 │
│                                         │
│  Loại nhiệm vụ:                        │
│  ┌─────────────────────────────────┐   │
│  │ [○──] 📋 Nhiệm vụ thường xuyên │   │
│  │       Click để đổi thành đột phá│   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │   ⚠️ Nhập ít nhất 10 ký tự...  │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ℹ️ Trạng thái: Backend đang chạy...   │
└─────────────────────────────────────────┘
```

---

## 🔍 Ví Dụ Thực Tế

### Ví Dụ 1: Tạo Nhiệm Vụ Thường Xuyên

1. Nhập: "Tổ chức họp hành chính định kỳ hàng tuần"
2. Giữ nút gạt ở vị trí xám (không click)
3. Click "LƯU NHIỆM VỤ"
4. Thấy thông báo thành công ✅

### Ví Dụ 2: Tạo Nhiệm Vụ Đột Phá

1. Nhập: "Triển khai hệ thống quản lý điện tử toàn bộ quy trình"
2. Click vào nút gạt → chuyển sang cam
3. Click "LƯU NHIỆM VỤ"
4. Thấy thông báo thành công ✅

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề:
1. Kiểm tra backend đang chạy: http://localhost:8080
2. Kiểm tra console trình duyệt (F12) để xem lỗi
3. Xem file HUONG_DAN_CHAY.md để biết cách khởi động lại hệ thống
4. Chạy `bash fix-postgres-error.sh` nếu gặp lỗi database

---

**Phiên bản:** 1.0  
**Cập nhật:** Tháng 12/2024
