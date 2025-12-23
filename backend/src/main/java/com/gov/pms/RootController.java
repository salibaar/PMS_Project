package com.gov.pms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public ResponseEntity<?> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Planning Management System (PMS)");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("message", "Backend API đang chạy thành công!");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("POST /api/v1/planning/objectives", "Tạo nhiệm vụ mới");
        endpoints.put("GET /api/v1/health", "Kiểm tra trạng thái hệ thống");
        
        response.put("endpoints", endpoints);
        
        Map<String, String> frontend = new HashMap<>();
        frontend.put("url", "http://localhost:3000");
        frontend.put("description", "Truy cập giao diện web tại địa chỉ này");
        
        response.put("frontend", frontend);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/v1/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "PMS Backend API");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
