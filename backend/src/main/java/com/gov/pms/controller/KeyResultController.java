package com.gov.pms.controller;

import com.gov.pms.dto.KeyResultDTO;
import com.gov.pms.entity.KeyResult;
import com.gov.pms.service.KeyResultService;
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
public class KeyResultController {
    
    @Autowired
    private KeyResultService keyResultService;
    
    @GetMapping("/{id}")
    public ResponseEntity<KeyResultDTO> getKeyResultById(@PathVariable UUID id) {
        KeyResult keyResult = keyResultService.getKeyResultById(id);
        return ResponseEntity.ok(keyResultService.convertToDTO(keyResult));
    }
    
    @PostMapping
    public ResponseEntity<KeyResultDTO> createKeyResult(@Valid @RequestBody KeyResultDTO keyResultDTO) {
        KeyResult keyResult = keyResultService.createKeyResult(keyResultDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(keyResultService.convertToDTO(keyResult));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<KeyResultDTO> updateKeyResult(
            @PathVariable UUID id,
            @Valid @RequestBody KeyResultDTO keyResultDTO) {
        KeyResult keyResult = keyResultService.updateKeyResult(id, keyResultDTO);
        return ResponseEntity.ok(keyResultService.convertToDTO(keyResult));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeyResult(@PathVariable UUID id) {
        keyResultService.deleteKeyResult(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/objective/{objectiveId}")
    public ResponseEntity<List<KeyResultDTO>> getKeyResultsByObjectiveId(@PathVariable UUID objectiveId) {
        List<KeyResult> keyResults = keyResultService.getKeyResultsByObjectiveId(objectiveId);
        List<KeyResultDTO> dtos = keyResults.stream()
            .map(keyResultService::convertToDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }
}
