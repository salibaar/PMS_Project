package com.gov.pms.service;

import com.gov.pms.dto.ObjectiveDTO;
import com.gov.pms.entity.Objective;

import java.util.List;
import java.util.UUID;

public interface ObjectiveService {
    
    Objective createObjective(ObjectiveDTO objectiveDTO, UUID userId);
    
    Objective updateObjective(UUID id, ObjectiveDTO objectiveDTO);
    
    void deleteObjective(UUID id);
    
    Objective getObjectiveById(UUID id);
    
    List<Objective> getObjectivesByPlanId(UUID planId);
    
    List<Objective> getRootObjectivesByPlanId(UUID planId);
    
    List<Objective> getChildObjectives(UUID parentId);
    
    List<Objective> getBreakthroughObjectives(UUID planId);
    
    List<Objective> getAllObjectives();
    
    ObjectiveDTO convertToDTO(Objective objective);
    
    Objective convertToEntity(ObjectiveDTO objectiveDTO);
}
