package com.gov.pms.controller;

import com.gov.pms.dto.KeyResultDTO;
import com.gov.pms.entity.KeyResult;
import com.gov.pms.service.KeyResultService;
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
@RequestMapping("/api/v1/key-results")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Key Results", description = "API quản lý Mục tiêu then chốt (Key Results)")
public class KeyResultController {
    
    @Autowired
    private KeyResultService keyResultService;
    
    @Operation(summary = "Lấy mục tiêu then chốt theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<KeyResultDTO> getKeyResultById(@Parameter(description = "ID mục tiêu") @PathVariable UUID id) {
        KeyResult keyResult = keyResultService.getKeyResultById(id);
        return ResponseEntity.ok(keyResultService.convertToDTO(keyResult));
    }
    
    @Operation(summary = "Tạo mục tiêu then chốt mới")
    @PostMapping
    public ResponseEntity<KeyResultDTO> createKeyResult(@Valid @RequestBody KeyResultDTO keyResultDTO) {
        KeyResult keyResult = keyResultService.createKeyResult(keyResultDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(keyResultService.convertToDTO(keyResult));
    }
    
    @Operation(summary = "Cập nhật mục tiêu then chốt")
    @PutMapping("/{id}")
    public ResponseEntity<KeyResultDTO> updateKeyResult(
            @Parameter(description = "ID mục tiêu") @PathVariable UUID id,
            @Valid @RequestBody KeyResultDTO keyResultDTO) {
        KeyResult keyResult = keyResultService.updateKeyResult(id, keyResultDTO);
        return ResponseEntity.ok(keyResultService.convertToDTO(keyResult));
    }
    
    @Operation(summary = "Xóa mục tiêu then chốt")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeyResult(@Parameter(description = "ID mục tiêu") @PathVariable UUID id) {
        keyResultService.deleteKeyResult(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Lấy mục tiêu then chốt theo nhiệm vụ")
    @GetMapping("/objective/{objectiveId}")
    public ResponseEntity<List<KeyResultDTO>> getKeyResultsByObjectiveId(@Parameter(description = "ID nhiệm vụ") @PathVariable UUID objectiveId) {
        List<KeyResult> keyResults = keyResultService.getKeyResultsByObjectiveId(objectiveId);
        List<KeyResultDTO> dtos = keyResults.stream()
            .map(keyResultService::convertToDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }
}
