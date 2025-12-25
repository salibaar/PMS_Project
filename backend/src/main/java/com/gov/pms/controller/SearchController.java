package com.gov.pms.controller;

import com.gov.pms.entity.Objective;
import com.gov.pms.entity.Plan;
import com.gov.pms.repository.ObjectiveRepository;
import com.gov.pms.repository.PlanRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search & Filter", description = "API tìm kiếm và lọc nâng cao")
public class SearchController {
    
    private final PlanRepository planRepository;
    private final ObjectiveRepository objectiveRepository;
    
    @GetMapping("/plans")
    @Operation(summary = "Tìm kiếm và lọc kế hoạch nâng cao",
               description = "Tìm kiếm full-text và lọc theo nhiều tiêu chí")
    public ResponseEntity<Page<Plan>> searchPlans(
            @Parameter(description = "Từ khóa tìm kiếm (title, description)") 
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Năm") 
            @RequestParam(required = false) Integer year,
            @Parameter(description = "Trạng thái (DRAFT, ACTIVE, COMPLETED, ARCHIVED)") 
            @RequestParam(required = false) Plan.PlanStatus status,
            @Parameter(description = "Tiến độ tối thiểu (0-100)") 
            @RequestParam(required = false) Integer minProgress,
            @Parameter(description = "Tiến độ tối đa (0-100)") 
            @RequestParam(required = false) Integer maxProgress,
            Pageable pageable) {
        
        Page<Plan> plans;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            plans = planRepository.fullTextSearch(keyword, pageable);
        } else {
            plans = planRepository.findByFilters(year, status, minProgress, maxProgress, pageable);
        }
        
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/objectives")
    @Operation(summary = "Tìm kiếm và lọc nhiệm vụ nâng cao",
               description = "Tìm kiếm full-text và lọc theo nhiều tiêu chí")
    public ResponseEntity<List<Objective>> searchObjectives(
            @Parameter(description = "Từ khóa tìm kiếm (title, description)") 
            @RequestParam(required = false) String keyword,
            @Parameter(description = "ID của Plan") 
            @RequestParam(required = false) UUID planId,
            @Parameter(description = "Trạng thái") 
            @RequestParam(required = false) Objective.ObjectiveStatus status,
            @Parameter(description = "Tiến độ tối thiểu (0-100)") 
            @RequestParam(required = false) Integer minProgress,
            @Parameter(description = "Tiến độ tối đa (0-100)") 
            @RequestParam(required = false) Integer maxProgress,
            @Parameter(description = "Chỉ nhiệm vụ đột phá") 
            @RequestParam(required = false) Boolean isBreakthrough) {
        
        List<Objective> objectives;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            objectives = objectiveRepository.fullTextSearch(keyword);
        } else {
            objectives = objectiveRepository.findByFilters(planId, status, minProgress, maxProgress, isBreakthrough);
        }
        
        return ResponseEntity.ok(objectives);
    }
    
    @GetMapping("/global")
    @Operation(summary = "Tìm kiếm toàn cục",
               description = "Tìm kiếm cùng lúc trong Plans và Objectives")
    public ResponseEntity<Map<String, Object>> globalSearch(
            @Parameter(description = "Từ khóa tìm kiếm") 
            @RequestParam String keyword,
            Pageable pageable) {
        
        Page<Plan> plans = planRepository.fullTextSearch(keyword, pageable);
        List<Objective> objectives = objectiveRepository.fullTextSearch(keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("plans", plans);
        result.put("objectives", objectives);
        result.put("totalPlans", plans.getTotalElements());
        result.put("totalObjectives", objectives.size());
        
        return ResponseEntity.ok(result);
    }
}
