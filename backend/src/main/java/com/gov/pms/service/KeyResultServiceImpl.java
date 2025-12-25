package com.gov.pms.service;

import com.gov.pms.dto.KeyResultDTO;
import com.gov.pms.entity.KeyResult;
import com.gov.pms.entity.Objective;
import com.gov.pms.exception.ResourceNotFoundException;
import com.gov.pms.repository.KeyResultRepository;
import com.gov.pms.repository.ObjectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class KeyResultServiceImpl implements KeyResultService {
    
    @Autowired
    private KeyResultRepository keyResultRepository;
    
    @Autowired
    private ObjectiveRepository objectiveRepository;
    
    @Override
    public KeyResult createKeyResult(KeyResultDTO keyResultDTO) {
        Objective objective = objectiveRepository.findById(keyResultDTO.getObjectiveId())
            .orElseThrow(() -> new ResourceNotFoundException("Objective", "id", keyResultDTO.getObjectiveId()));
        
        KeyResult keyResult = convertToEntity(keyResultDTO);
        keyResult.setObjective(objective);
        keyResult.setCreatedAt(LocalDateTime.now());
        keyResult.setUpdatedAt(LocalDateTime.now());
        
        return keyResultRepository.save(keyResult);
    }
    
    @Override
    public KeyResult updateKeyResult(UUID id, KeyResultDTO keyResultDTO) {
        KeyResult keyResult = getKeyResultById(id);
        
        keyResult.setDescription(keyResultDTO.getDescription());
        keyResult.setTargetValue(keyResultDTO.getTargetValue());
        keyResult.setCurrentValue(keyResultDTO.getCurrentValue());
        keyResult.setUnit(keyResultDTO.getUnit());
        
        if (keyResultDTO.getStatus() != null) {
            keyResult.setStatus(keyResultDTO.getStatus());
        }
        
        keyResult.setUpdatedAt(LocalDateTime.now());
        return keyResultRepository.save(keyResult);
    }
    
    @Override
    public void deleteKeyResult(UUID id) {
        KeyResult keyResult = getKeyResultById(id);
        keyResultRepository.delete(keyResult);
    }
    
    @Override
    @Transactional(readOnly = true)
    public KeyResult getKeyResultById(UUID id) {
        return keyResultRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("KeyResult", "id", id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<KeyResult> getKeyResultsByObjectiveId(UUID objectiveId) {
        return keyResultRepository.findByObjectiveId(objectiveId);
    }
    
    @Override
    public KeyResultDTO convertToDTO(KeyResult keyResult) {
        KeyResultDTO dto = new KeyResultDTO();
        dto.setId(keyResult.getId());
        dto.setObjectiveId(keyResult.getObjective().getId());
        dto.setDescription(keyResult.getDescription());
        dto.setTargetValue(keyResult.getTargetValue());
        dto.setCurrentValue(keyResult.getCurrentValue());
        dto.setUnit(keyResult.getUnit());
        dto.setStatus(keyResult.getStatus());
        dto.setCreatedAt(keyResult.getCreatedAt());
        dto.setUpdatedAt(keyResult.getUpdatedAt());
        return dto;
    }
    
    @Override
    public KeyResult convertToEntity(KeyResultDTO keyResultDTO) {
        KeyResult keyResult = new KeyResult();
        keyResult.setDescription(keyResultDTO.getDescription());
        keyResult.setTargetValue(keyResultDTO.getTargetValue());
        keyResult.setCurrentValue(keyResultDTO.getCurrentValue() != null ? 
            keyResultDTO.getCurrentValue() : BigDecimal.ZERO);
        keyResult.setUnit(keyResultDTO.getUnit());
        
        if (keyResultDTO.getStatus() != null) {
            keyResult.setStatus(keyResultDTO.getStatus());
        } else {
            keyResult.setStatus(KeyResult.KeyResultStatus.NOT_STARTED);
        }
        
        return keyResult;
    }
}
