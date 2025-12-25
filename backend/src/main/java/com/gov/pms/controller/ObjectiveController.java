package com.gov.pms.controller;

import com.gov.pms.dto.ObjectiveDTO;
import com.gov.pms.entity.Objective;
import com.gov.pms.service.ObjectiveService;
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
public class ObjectiveController {
    
    @Autowired
    private ObjectiveService objectiveService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ObjectiveDTO> getObjectiveById(@PathVariable UUID id) {
        Objective objective = objectiveService.getObjectiveById(id);
        return ResponseEntity.ok(objectiveService.convertToDTO(objective));
    }
    
    @PostMapping
    public ResponseEntity<ObjectiveDTO> createObjective(@Valid @RequestBody ObjectiveDTO objectiveDTO) {
        // Default user ID - in production, this would come from authentication
        UUID userId = UUID.randomUUID();
        Objective objective = objectiveService.createObjective(objectiveDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(objectiveService.convertToDTO(objective));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ObjectiveDTO> updateObjective(
            @PathVariable UUID id,
            @Valid @RequestBody ObjectiveDTO objectiveDTO) {
        Objective objective = objectiveService.updateObjective(id, objectiveDTO);
        return ResponseEntity.ok(objectiveService.convertToDTO(objective));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjective(@PathVariable UUID id) {
        objectiveService.deleteObjective(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<ObjectiveDTO>> getObjectivesByPlanId(@PathVariable UUID planId) {
        List<Objective> objectives = objectiveService.getRootObjectivesByPlanId(planId);
        List<ObjectiveDTO> dtos = objectives.stream()
            .map(objectiveService::convertToDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}/children")
    public ResponseEntity<List<ObjectiveDTO>> getChildObjectives(@PathVariable UUID id) {
        List<Objective> children = objectiveService.getChildObjectives(id);
        List<ObjectiveDTO> dtos = children.stream()
            .map(objectiveService::convertToDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/plan/{planId}/breakthrough")
    public ResponseEntity<List<ObjectiveDTO>> getBreakthroughObjectives(@PathVariable UUID planId) {
        List<Objective> objectives = objectiveService.getBreakthroughObjectives(planId);
        List<ObjectiveDTO> dtos = objectives.stream()
            .map(objectiveService::convertToDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }
}
