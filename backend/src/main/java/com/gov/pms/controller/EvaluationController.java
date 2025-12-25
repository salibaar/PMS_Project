package com.gov.pms.controller;

import com.gov.pms.dto.EvaluationDTO;
import com.gov.pms.entity.Evaluation;
import com.gov.pms.security.CustomUserDetailsService;
import com.gov.pms.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/evaluations")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Evaluations", description = "API quản lý đánh giá (Evaluations)")
public class EvaluationController {
    
    @Autowired
    private EvaluationService evaluationService;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Operation(summary = "Tạo đánh giá mới", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<EvaluationDTO> createEvaluation(
            @Valid @RequestBody EvaluationDTO evaluationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        var user = customUserDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        Evaluation evaluation = evaluationService.createEvaluation(evaluationDTO, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(evaluationService.convertToDTO(evaluation));
    }
    
    @Operation(summary = "Cập nhật đánh giá", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationDTO> updateEvaluation(
            @Parameter(description = "ID đánh giá") @PathVariable UUID id,
            @Valid @RequestBody EvaluationDTO evaluationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        var user = customUserDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        Evaluation evaluation = evaluationService.updateEvaluation(id, evaluationDTO, user.getId());
        return ResponseEntity.ok(evaluationService.convertToDTO(evaluation));
    }
    
    @Operation(summary = "Xóa đánh giá", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(
            @Parameter(description = "ID đánh giá") @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        var user = customUserDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        evaluationService.deleteEvaluation(id, user.getId());
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Lấy đánh giá theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationDTO> getEvaluationById(
            @Parameter(description = "ID đánh giá") @PathVariable UUID id) {
        
        Evaluation evaluation = evaluationService.getEvaluationById(id);
        return ResponseEntity.ok(evaluationService.convertToDTO(evaluation));
    }
    
    @Operation(summary = "Lấy đánh giá theo objective (có phân trang)")
    @GetMapping("/objective/{objectiveId}")
    public ResponseEntity<Map<String, Object>> getEvaluationsByObjectiveId(
            @Parameter(description = "ID nhiệm vụ") @PathVariable UUID objectiveId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Evaluation> evaluationPage = evaluationService.getEvaluationsByObjectiveId(objectiveId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("evaluations", evaluationPage.getContent().stream()
            .map(evaluationService::convertToDTO)
            .collect(Collectors.toList()));
        response.put("currentPage", evaluationPage.getNumber());
        response.put("totalItems", evaluationPage.getTotalElements());
        response.put("totalPages", evaluationPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lấy tất cả đánh giá theo objective")
    @GetMapping("/objective/{objectiveId}/all")
    public ResponseEntity<List<EvaluationDTO>> getAllEvaluations(
            @Parameter(description = "ID nhiệm vụ") @PathVariable UUID objectiveId) {
        
        List<Evaluation> evaluations = evaluationService.getEvaluationsByObjectiveId(objectiveId);
        List<EvaluationDTO> dtos = evaluations.stream()
            .map(evaluationService::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @Operation(summary = "Cập nhật trạng thái đánh giá", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/status")
    public ResponseEntity<EvaluationDTO> updateStatus(
            @Parameter(description = "ID đánh giá") @PathVariable UUID id,
            @RequestParam Evaluation.EvaluationStatus status) {
        
        Evaluation evaluation = evaluationService.updateEvaluationStatus(id, status);
        return ResponseEntity.ok(evaluationService.convertToDTO(evaluation));
    }
    
    @Operation(summary = "Lấy rating trung bình theo objective")
    @GetMapping("/objective/{objectiveId}/average-rating")
    public ResponseEntity<Map<String, Object>> getAverageRating(
            @Parameter(description = "ID nhiệm vụ") @PathVariable UUID objectiveId) {
        
        Double avgRating = evaluationService.getAverageRatingByObjectiveId(objectiveId);
        long count = evaluationService.countEvaluationsByObjectiveId(objectiveId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", avgRating);
        response.put("totalEvaluations", count);
        
        return ResponseEntity.ok(response);
    }
}
