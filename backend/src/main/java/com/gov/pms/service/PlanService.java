package com.gov.pms.service;

import com.gov.pms.dto.PlanDTO;
import com.gov.pms.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PlanService {
    
    Plan createPlan(PlanDTO planDTO, UUID userId);
    
    Plan updatePlan(UUID id, PlanDTO planDTO);
    
    void deletePlan(UUID id);
    
    Plan getPlanById(UUID id);
    
    Page<Plan> getAllPlans(Pageable pageable);
    
    Page<Plan> getPlansByYear(Integer year, Pageable pageable);
    
    Page<Plan> searchPlans(String keyword, Pageable pageable);
    
    java.util.List<Plan> getPlansByYear(Integer year);
    
    PlanDTO convertToDTO(Plan plan);
    
    Plan convertToEntity(PlanDTO planDTO);
}
