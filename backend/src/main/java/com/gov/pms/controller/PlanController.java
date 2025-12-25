package com.gov.pms.controller;

import com.gov.pms.dto.PlanDTO;
import com.gov.pms.entity.Plan;
import com.gov.pms.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Plans", description = "API quản lý Kế hoạch (Plans)")
public class PlanController {
    
    @Autowired
    private PlanService planService;
    
    @Operation(summary = "Lấy danh sách kế hoạch", description = "Lấy danh sách tất cả kế hoạch với phân trang và sắp xếp")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPlans(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng mỗi trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Hướng sắp xếp (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDir) {
        
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
    
    @Operation(summary = "Lấy kế hoạch theo ID", description = "Lấy thông tin chi tiết một kế hoạch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy kế hoạch")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlanById(@Parameter(description = "ID kế hoạch") @PathVariable UUID id) {
        Plan plan = planService.getPlanById(id);
        return ResponseEntity.ok(planService.convertToDTO(plan));
    }
    
    @Operation(summary = "Tạo kế hoạch mới", description = "Tạo một kế hoạch mới")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping
    public ResponseEntity<PlanDTO> createPlan(@Valid @RequestBody PlanDTO planDTO) {
        // Default user ID - in production, this would come from authentication
        UUID userId = UUID.randomUUID();
        Plan plan = planService.createPlan(planDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(planService.convertToDTO(plan));
    }
    
    @Operation(summary = "Cập nhật kế hoạch", description = "Cập nhật thông tin kế hoạch")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy kế hoạch")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PlanDTO> updatePlan(
            @Parameter(description = "ID kế hoạch") @PathVariable UUID id, 
            @Valid @RequestBody PlanDTO planDTO) {
        Plan plan = planService.updatePlan(id, planDTO);
        return ResponseEntity.ok(planService.convertToDTO(plan));
    }
    
    @Operation(summary = "Xóa kế hoạch", description = "Xóa kế hoạch (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy kế hoạch")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@Parameter(description = "ID kế hoạch") @PathVariable UUID id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Lấy kế hoạch theo năm", description = "Lọc kế hoạch theo năm")
    @GetMapping("/year/{year}")
    public ResponseEntity<Map<String, Object>> getPlansByYear(
            @Parameter(description = "Năm") @PathVariable Integer year,
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
    
    @Operation(summary = "Tìm kiếm kế hoạch", description = "Tìm kiếm kế hoạch theo từ khóa")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPlans(
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam String keyword,
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
