package com.gov.pms;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/planning")
@CrossOrigin(origins = "http://localhost:3000")
public class PlanningController {

    private static final Logger logger = LoggerFactory.getLogger(PlanningController.class);

    @PostMapping("/objectives")
    public ResponseEntity<?> createObjective(@Valid @RequestBody ObjectiveRequest request) {
        logger.info("✅ Đã nhận nhiệm vụ: {} (Đột phá: {})", 
                    request.getContent(), request.getIsBreakthrough());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Nhiệm vụ đã được lưu thành công");
        response.put("data", request);
        
        return ResponseEntity.ok(response);
    }
} 

@Data
class ObjectiveRequest {
    @NotNull(message = "Plan ID không được để trống")
    private Integer planId;
    
    @NotBlank(message = "Nội dung nhiệm vụ không được để trống")
    @Size(min = 10, max = 500, message = "Nội dung phải từ 10 đến 500 ký tự")
    private String content;
    
    @NotNull(message = "Trạng thái đột phá không được để trống")
    private Boolean isBreakthrough;
    
    private List<String> keyResults;
}