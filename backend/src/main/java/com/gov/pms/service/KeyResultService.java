package com.gov.pms.service;

import com.gov.pms.dto.KeyResultDTO;
import com.gov.pms.entity.KeyResult;

import java.util.List;
import java.util.UUID;

public interface KeyResultService {
    
    KeyResult createKeyResult(KeyResultDTO keyResultDTO);
    
    KeyResult updateKeyResult(UUID id, KeyResultDTO keyResultDTO);
    
    void deleteKeyResult(UUID id);
    
    KeyResult getKeyResultById(UUID id);
    
    List<KeyResult> getKeyResultsByObjectiveId(UUID objectiveId);
    
    KeyResultDTO convertToDTO(KeyResult keyResult);
    
    KeyResult convertToEntity(KeyResultDTO keyResultDTO);
}
