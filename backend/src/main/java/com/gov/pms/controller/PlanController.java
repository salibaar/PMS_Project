package com.gov.pms.controller;

import com.gov.pms.dto.PlanDTO;
import com.gov.pms.entity.Plan;
import com.gov.pms.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plans")
@CrossOrigin(origins = "http://localhost:3000")
public class PlanController {
    
    @Autowired
    private PlanService planService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Plan> planPage = planService.getAllPlans(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("plans", planPage.getContent().stream()
            .map(planService::convertToDTO).toList());
        response.put("currentPage", planPage.getNumber());
        response.put("totalItems", planPage.getTotalElements());
        response.put("totalPages", planPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable UUID id) {
        Plan plan = planService.getPlanById(id);
        return ResponseEntity.ok(planService.convertToDTO(plan));
    }
    
    @PostMapping
    public ResponseEntity<PlanDTO> createPlan(@Valid @RequestBody PlanDTO planDTO) {
        // Default user ID - in production, this would come from authentication
        UUID userId = UUID.randomUUID();
        Plan plan = planService.createPlan(planDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(planService.convertToDTO(plan));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PlanDTO> updatePlan(
            @PathVariable UUID id, 
            @Valid @RequestBody PlanDTO planDTO) {
        Plan plan = planService.updatePlan(id, planDTO);
        return ResponseEntity.ok(planService.convertToDTO(plan));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable UUID id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/year/{year}")
    public ResponseEntity<Map<String, Object>> getPlansByYear(
            @PathVariable Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Plan> planPage = planService.getPlansByYear(year, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("plans", planPage.getContent().stream()
            .map(planService::convertToDTO).toList());
        response.put("currentPage", planPage.getNumber());
        response.put("totalItems", planPage.getTotalElements());
        response.put("totalPages", planPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPlans(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Plan> planPage = planService.searchPlans(keyword, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("plans", planPage.getContent().stream()
            .map(planService::convertToDTO).toList());
        response.put("currentPage", planPage.getNumber());
        response.put("totalItems", planPage.getTotalElements());
        response.put("totalPages", planPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
}
