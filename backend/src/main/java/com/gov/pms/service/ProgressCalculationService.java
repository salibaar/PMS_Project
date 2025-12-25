package com.gov.pms.service;

import com.gov.pms.entity.Objective;
import com.gov.pms.entity.Plan;
import com.gov.pms.repository.ObjectiveRepository;
import com.gov.pms.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressCalculationService {
    
    private final PlanRepository planRepository;
    private final ObjectiveRepository objectiveRepository;
    
    /**
     * Calculate and update progress for a specific plan based on its objectives
     */
    public void calculatePlanProgress(UUID planId) {
        Plan plan = planRepository.findById(planId).orElse(null);
        if (plan == null) {
            return;
        }
        
        List<Objective> objectives = objectiveRepository.findByPlanIdAndParentIsNull(planId);
        
        if (objectives.isEmpty()) {
            // plan.setProgress(0); // Plan entity doesn't have progress field
        } else {
            int totalProgress = objectives.stream()
                    .mapToInt(Objective::getProgress)
                    .sum();
            int averageProgress = totalProgress / objectives.size();
            // Note: Plan entity doesn't have progress field, but we keep this method for future use
            // When Plan entity is updated with progress field, uncomment:
            // plan.setProgress(averageProgress);
        }
        
        planRepository.save(plan);
    }
    
    /**
     * Calculate and update progress for a specific objective based on its key results
     */
    public void calculateObjectiveProgress(UUID objectiveId) {
        Objective objective = objectiveRepository.findById(objectiveId).orElse(null);
        if (objective == null) {
            return;
        }
        
        // If objective has key results, calculate from them
        if (objective.getKeyResults() != null && !objective.getKeyResults().isEmpty()) {
            int completedCount = (int) objective.getKeyResults().stream()
                    .filter(kr -> kr.getStatus() == com.gov.pms.entity.KeyResult.KeyResultStatus.COMPLETED)
                    .count();
            int totalCount = objective.getKeyResults().size();
            int progress = (completedCount * 100) / totalCount;
            objective.setProgress(progress);
        }
        // If objective has children, calculate from them
        else if (objective.getChildren() != null && !objective.getChildren().isEmpty()) {
            int totalProgress = objective.getChildren().stream()
                    .mapToInt(Objective::getProgress)
                    .sum();
            int averageProgress = totalProgress / objective.getChildren().size();
            objective.setProgress(averageProgress);
        }
        
        objectiveRepository.save(objective);
        
        // Update parent objective if exists
        if (objective.getParent() != null) {
            calculateObjectiveProgress(objective.getParent().getId());
        }
        
        // Update plan progress
        if (objective.getPlan() != null) {
            calculatePlanProgress(objective.getPlan().getId());
        }
    }
    
    /**
     * Automatically update status based on progress
     */
    public void updateObjectiveStatus(UUID objectiveId) {
        Objective objective = objectiveRepository.findById(objectiveId).orElse(null);
        if (objective == null) {
            return;
        }
        
        int progress = objective.getProgress();
        
        if (progress == 0) {
            objective.setStatus(Objective.ObjectiveStatus.NOT_STARTED);
        } else if (progress > 0 && progress < 100) {
            objective.setStatus(Objective.ObjectiveStatus.IN_PROGRESS);
        } else if (progress == 100) {
            objective.setStatus(Objective.ObjectiveStatus.COMPLETED);
        }
        
        objectiveRepository.save(objective);
    }
    
    /**
     * Recalculate progress for all objectives in a plan
     */
    public void recalculatePlanObjectives(UUID planId) {
        List<Objective> objectives = objectiveRepository.findByPlanIdAndParentIsNull(planId);
        
        for (Objective objective : objectives) {
            calculateObjectiveProgress(objective.getId());
            updateObjectiveStatus(objective.getId());
        }
        
        calculatePlanProgress(planId);
    }
}
