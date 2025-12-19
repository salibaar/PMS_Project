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
    private String content;
    // ... các trường khác ...

    public String getContent() {
        return content;
    }
}