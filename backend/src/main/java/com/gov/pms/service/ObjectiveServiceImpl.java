package com.gov.pms.service;

import com.gov.pms.dto.ObjectiveDTO;
import com.gov.pms.entity.Objective;
import com.gov.pms.entity.Plan;
import com.gov.pms.exception.ResourceNotFoundException;
import com.gov.pms.repository.ObjectiveRepository;
import com.gov.pms.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ObjectiveServiceImpl implements ObjectiveService {
    
    @Autowired
    private ObjectiveRepository objectiveRepository;
    
    @Autowired
    private PlanRepository planRepository;
    
    @Override
    public Objective createObjective(ObjectiveDTO objectiveDTO, UUID userId) {
        Plan plan = planRepository.findById(objectiveDTO.getPlanId())
            .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", objectiveDTO.getPlanId()));
        
        Objective objective = convertToEntity(objectiveDTO);
        objective.setPlan(plan);
        objective.setCreatedBy(userId);
        objective.setCreatedAt(LocalDateTime.now());
        objective.setUpdatedAt(LocalDateTime.now());
        
        if (objectiveDTO.getParentId() != null) {
            Objective parent = getObjectiveById(objectiveDTO.getParentId());
            objective.setParent(parent);
        }
        
        return objectiveRepository.save(objective);
    }
    
    @Override
    public Objective updateObjective(UUID id, ObjectiveDTO objectiveDTO) {
        Objective objective = getObjectiveById(id);
        
        objective.setContent(objectiveDTO.getContent());
        objective.setIsBreakthrough(objectiveDTO.getIsBreakthrough());
        
        if (objectiveDTO.getOrderIndex() != null) {
            objective.setOrderIndex(objectiveDTO.getOrderIndex());
        }
        
        if (objectiveDTO.getStatus() != null) {
            objective.setStatus(objectiveDTO.getStatus());
        }
        
        if (objectiveDTO.getProgress() != null) {
            objective.setProgress(objectiveDTO.getProgress());
        }
        
        objective.setUpdatedAt(LocalDateTime.now());
        return objectiveRepository.save(objective);
    }
    
    @Override
    public void deleteObjective(UUID id) {
        Objective objective = getObjectiveById(id);
        objectiveRepository.delete(objective); // Soft delete
    }
    
    @Override
    @Transactional(readOnly = true)
    public Objective getObjectiveById(UUID id) {
        return objectiveRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Objective", "id", id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Objective> getObjectivesByPlanId(UUID planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));
        return plan.getObjectives();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Objective> getRootObjectivesByPlanId(UUID planId) {
        return objectiveRepository.findByPlanIdAndParentIsNull(planId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Objective> getChildObjectives(UUID parentId) {
        return objectiveRepository.findByParentId(parentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Objective> getBreakthroughObjectives(UUID planId) {
        return objectiveRepository.findByPlanIdAndIsBreakthroughTrue(planId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Objective> getAllObjectives() {
        return objectiveRepository.findAll();
    }
    
    @Override
    public ObjectiveDTO convertToDTO(Objective objective) {
        ObjectiveDTO dto = new ObjectiveDTO();
        dto.setId(objective.getId());
        dto.setPlanId(objective.getPlan().getId());
        
        if (objective.getParent() != null) {
            dto.setParentId(objective.getParent().getId());
        }
        
        dto.setContent(objective.getContent());
        dto.setIsBreakthrough(objective.getIsBreakthrough());
        dto.setOrderIndex(objective.getOrderIndex());
        dto.setStatus(objective.getStatus());
        dto.setProgress(objective.getProgress());
        dto.setCreatedBy(objective.getCreatedBy());
        dto.setCreatedAt(objective.getCreatedAt());
        dto.setUpdatedAt(objective.getUpdatedAt());
        
        return dto;
    }
    
    @Override
    public Objective convertToEntity(ObjectiveDTO objectiveDTO) {
        Objective objective = new Objective();
        objective.setContent(objectiveDTO.getContent());
        objective.setIsBreakthrough(objectiveDTO.getIsBreakthrough());
        objective.setOrderIndex(objectiveDTO.getOrderIndex() != null ? objectiveDTO.getOrderIndex() : 0);
        
        if (objectiveDTO.getStatus() != null) {
            objective.setStatus(objectiveDTO.getStatus());
        } else {
            objective.setStatus(Objective.ObjectiveStatus.NOT_STARTED);
        }
        
        objective.setProgress(objectiveDTO.getProgress() != null ? objectiveDTO.getProgress() : 0);
        
        return objective;
    }
}
