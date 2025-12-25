package com.gov.pms.controller;

import com.gov.pms.service.ProgressCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Tag(name = "Progress Tracking", description = "API tính toán và theo dõi tiến độ tự động")
@SecurityRequirement(name = "bearer-token")
public class ProgressController {
    
    private final ProgressCalculationService progressCalculationService;
    
    @PostMapping("/objective/{id}/calculate")
    @Operation(summary = "Tính toán tiến độ objective",
               description = "Tự động tính toán tiến độ dựa trên key results và sub-objectives")
    public ResponseEntity<Map<String, String>> calculateObjectiveProgress(
            @Parameter(description = "ID của Objective") @PathVariable UUID id) {
        
        progressCalculationService.calculateObjectiveProgress(id);
        progressCalculationService.updateObjectiveStatus(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Objective progress calculated successfully");
        response.put("objectiveId", id.toString());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/plan/{id}/calculate")
    @Operation(summary = "Tính toán tiến độ plan",
               description = "Tự động tính toán tiến độ plan dựa trên tất cả objectives")
    public ResponseEntity<Map<String, String>> calculatePlanProgress(
            @Parameter(description = "ID của Plan") @PathVariable UUID id) {
        
        progressCalculationService.calculatePlanProgress(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Plan progress calculated successfully");
        response.put("planId", id.toString());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/plan/{id}/recalculate-all")
    @Operation(summary = "Tính lại toàn bộ tiến độ của plan",
               description = "Tính lại tiến độ cho tất cả objectives và plan")
    public ResponseEntity<Map<String, String>> recalculateAllProgress(
            @Parameter(description = "ID của Plan") @PathVariable UUID id) {
        
        progressCalculationService.recalculatePlanObjectives(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "All progress recalculated successfully");
        response.put("planId", id.toString());
        
        return ResponseEntity.ok(response);
    }
}
