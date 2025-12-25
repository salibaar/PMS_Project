package com.gov.pms.service;

import com.gov.pms.dto.EvaluationDTO;
import com.gov.pms.entity.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EvaluationService {
    
    Evaluation createEvaluation(EvaluationDTO evaluationDTO, UUID userId);
    
    Evaluation updateEvaluation(UUID id, EvaluationDTO evaluationDTO, UUID userId);
    
    void deleteEvaluation(UUID id, UUID userId);
    
    Evaluation getEvaluationById(UUID id);
    
    Page<Evaluation> getEvaluationsByObjectiveId(UUID objectiveId, Pageable pageable);
    
    List<Evaluation> getEvaluationsByObjectiveId(UUID objectiveId);
    
    Evaluation updateEvaluationStatus(UUID id, Evaluation.EvaluationStatus status);
    
    Double getAverageRatingByObjectiveId(UUID objectiveId);
    
    long countEvaluationsByObjectiveId(UUID objectiveId);
    
    EvaluationDTO convertToDTO(Evaluation evaluation);
}
