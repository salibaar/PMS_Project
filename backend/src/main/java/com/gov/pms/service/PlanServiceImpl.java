package com.gov.pms.service;

import com.gov.pms.dto.PlanDTO;
import com.gov.pms.entity.Plan;
import com.gov.pms.exception.ResourceNotFoundException;
import com.gov.pms.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PlanServiceImpl implements PlanService {
    
    @Autowired
    private PlanRepository planRepository;
    
    @Override
    public Plan createPlan(PlanDTO planDTO, UUID userId) {
        Plan plan = convertToEntity(planDTO);
        plan.setCreatedBy(userId);
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        return planRepository.save(plan);
    }
    
    @Override
    public Plan updatePlan(UUID id, PlanDTO planDTO) {
        Plan plan = getPlanById(id);
        
        plan.setYear(planDTO.getYear());
        plan.setTitle(planDTO.getTitle());
        plan.setDescription(planDTO.getDescription());
        
        if (planDTO.getStatus() != null) {
            plan.setStatus(planDTO.getStatus());
        }
        
        plan.setUpdatedAt(LocalDateTime.now());
        return planRepository.save(plan);
    }
    
    @Override
    public void deletePlan(UUID id) {
        Plan plan = getPlanById(id);
        planRepository.delete(plan); // Soft delete through @SQLDelete annotation
    }
    
    @Override
    @Transactional(readOnly = true)
    public Plan getPlanById(UUID id) {
        return planRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Plan> getAllPlans(Pageable pageable) {
        return planRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Plan> getPlansByYear(Integer year, Pageable pageable) {
        return planRepository.findByYear(year, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Plan> searchPlans(String keyword, Pageable pageable) {
        return planRepository.searchByTitle(keyword, pageable);
    }
    
    @Override
    public PlanDTO convertToDTO(Plan plan) {
        PlanDTO dto = new PlanDTO();
        dto.setId(plan.getId());
        dto.setYear(plan.getYear());
        dto.setTitle(plan.getTitle());
        dto.setDescription(plan.getDescription());
        dto.setStatus(plan.getStatus());
        dto.setCreatedBy(plan.getCreatedBy());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        return dto;
    }
    
    @Override
    public Plan convertToEntity(PlanDTO planDTO) {
        Plan plan = new Plan();
        plan.setYear(planDTO.getYear());
        plan.setTitle(planDTO.getTitle());
        plan.setDescription(planDTO.getDescription());
        
        if (planDTO.getStatus() != null) {
            plan.setStatus(planDTO.getStatus());
        } else {
            plan.setStatus(Plan.PlanStatus.DRAFT);
        }
        
        return plan;
    }
}
