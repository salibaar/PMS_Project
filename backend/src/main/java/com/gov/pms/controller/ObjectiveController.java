package com.gov.pms.controller;

import com.gov.pms.dto.ObjectiveDTO;
import com.gov.pms.entity.Objective;
import com.gov.pms.service.ObjectiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/objectives")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Objectives", description = "API quản lý Nhiệm vụ (Objectives)")
public class ObjectiveController {
    
    @Autowired
    private ObjectiveService objectiveService;
    
    @Operation(summary = "Lấy nhiệm vụ theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ObjectiveDTO> getObjectiveById(@Parameter(description = "ID nhiệm vụ") @PathVariable UUID id) {
        Objective objective = objectiveService.getObjectiveById(id);
        return ResponseEntity.ok(objectiveService.convertToDTO(objective));
    }
    
    @Operation(summary = "Tạo nhiệm vụ mới")
    @PostMapping
    public ResponseEntity<ObjectiveDTO> createObjective(@Valid @RequestBody ObjectiveDTO objectiveDTO) {
        // Default user ID - in production, this would come from authentication
        UUID userId = UUID.randomUUID();
        Objective objective = objectiveService.createObjective(objectiveDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(objectiveService.convertToDTO(objective));
    }
    
    @Operation(summary = "Cập nhật nhiệm vụ")
    @PutMapping("/{id}")
    public ResponseEntity<ObjectiveDTO> updateObjective(
            @Parameter(description = "ID nhiệm vụ") @PathVariable UUID id,
            @Valid @RequestBody ObjectiveDTO objectiveDTO) {
        Objective objective = objectiveService.updateObjective(id, objectiveDTO);
        return ResponseEntity.ok(objectiveService.convertToDTO(objective));
    }
    
    @Operation(summary = "Xóa nhiệm vụ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjective(@Parameter(description = "ID nhiệm vụ") @PathVariable UUID id) {
        objectiveService.deleteObjective(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Lấy nhiệm vụ theo kế hoạch")
    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<ObjectiveDTO>> getObjectivesByPlanId(@Parameter(description = "ID kế hoạch") @PathVariable UUID planId) {
        List<Objective> objectives = objectiveService.getRootObjectivesByPlanId(planId);
        List<ObjectiveDTO> dtos = objectives.stream()
            .map(objectiveService::convertToDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }
    
    @Operation(summary = "Lấy nhiệm vụ con")
    @GetMapping("/{id}/children")
    public ResponseEntity<List<ObjectiveDTO>> getChildObjectives(@Parameter(description = "ID nhiệm vụ cha") @PathVariable UUID id) {
        List<Objective> children = objectiveService.getChildObjectives(id);
        List<ObjectiveDTO> dtos = children.stream()
            .map(objectiveService::convertToDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }
    
    @Operation(summary = "Lấy nhiệm vụ đột phá")
    @GetMapping("/plan/{planId}/breakthrough")
    public ResponseEntity<List<ObjectiveDTO>> getBreakthroughObjectives(@Parameter(description = "ID kế hoạch") @PathVariable UUID planId) {
        List<Objective> objectives = objectiveService.getBreakthroughObjectives(planId);
        List<ObjectiveDTO> dtos = objectives.stream()
            .map(objectiveService::convertToDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }
}
