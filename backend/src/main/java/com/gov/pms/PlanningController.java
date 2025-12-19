package com.gov.pms;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.Data;
import java.util.List;

@RestController
@RequestMapping("/api/v1/planning")
@CrossOrigin(origins = "http://localhost:3000")
public class PlanningController {

    @PostMapping("/objectives")
    public ResponseEntity<?> createObjective(@Valid @RequestBody ObjectiveRequest request) {
        System.out.println("✅ Đã nhận nhiệm vụ an toàn: " + request.getContent());
        return ResponseEntity.ok(request);
    }
}

@Data
class ObjectiveRequest {
    @NotNull(message = "Thiếu ID kế hoạch")
    private Long planId;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(min = 5, max = 500, message = "Nội dung phải từ 5 đến 500 ký tự")
    private String content;

    private boolean isBreakthrough;
    private List<Object> keyResults;

    // Thủ công thêm Getter để đảm bảo trình biên dịch Maven luôn tìm thấy
    public String getContent() {
        return content;
    }
}