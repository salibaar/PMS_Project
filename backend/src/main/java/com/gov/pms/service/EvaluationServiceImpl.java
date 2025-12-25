package com.gov.pms.service;

import com.gov.pms.dto.EvaluationDTO;
import com.gov.pms.entity.Evaluation;
import com.gov.pms.exception.ResourceNotFoundException;
import com.gov.pms.repository.EvaluationRepository;
import com.gov.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EvaluationServiceImpl implements EvaluationService {
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Evaluation createEvaluation(EvaluationDTO evaluationDTO, UUID userId) {
        // Check if user already evaluated this objective
        evaluationRepository.findByObjectiveIdAndUserId(
            evaluationDTO.getObjectiveId(), userId
        ).ifPresent(existing -> {
            throw new IllegalStateException("Bạn đã đánh giá nhiệm vụ này rồi");
        });
        
        Evaluation evaluation = new Evaluation();
        evaluation.setObjectiveId(evaluationDTO.getObjectiveId());
        evaluation.setUserId(userId);
        evaluation.setRating(evaluationDTO.getRating());
        evaluation.setFeedback(evaluationDTO.getFeedback());
        evaluation.setStatus(Evaluation.EvaluationStatus.PENDING);
        evaluation.setCreatedAt(LocalDateTime.now());
        evaluation.setUpdatedAt(LocalDateTime.now());
        
        return evaluationRepository.save(evaluation);
    }
    
    @Override
    public Evaluation updateEvaluation(UUID id, EvaluationDTO evaluationDTO, UUID userId) {
        Evaluation evaluation = getEvaluationById(id);
        
        // Only the author can update their evaluation
        if (!evaluation.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật đánh giá này");
        }
        
        evaluation.setRating(evaluationDTO.getRating());
        evaluation.setFeedback(evaluationDTO.getFeedback());
        evaluation.setUpdatedAt(LocalDateTime.now());
        
        return evaluationRepository.save(evaluation);
    }
    
    @Override
    public void deleteEvaluation(UUID id, UUID userId) {
        Evaluation evaluation = getEvaluationById(id);
        
        // Only the author can delete their evaluation
        if (!evaluation.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền xóa đánh giá này");
        }
        
        evaluationRepository.delete(evaluation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Evaluation getEvaluationById(UUID id) {
        return evaluationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Evaluation", "id", id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Evaluation> getEvaluationsByObjectiveId(UUID objectiveId, Pageable pageable) {
        return evaluationRepository.findByObjectiveId(objectiveId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Evaluation> getEvaluationsByObjectiveId(UUID objectiveId) {
        return evaluationRepository.findByObjectiveId(objectiveId);
    }
    
    @Override
    public Evaluation updateEvaluationStatus(UUID id, Evaluation.EvaluationStatus status) {
        Evaluation evaluation = getEvaluationById(id);
        evaluation.setStatus(status);
        evaluation.setUpdatedAt(LocalDateTime.now());
        return evaluationRepository.save(evaluation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByObjectiveId(UUID objectiveId) {
        Double avg = evaluationRepository.getAverageRatingByObjectiveId(objectiveId);
        return avg != null ? avg : 0.0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countEvaluationsByObjectiveId(UUID objectiveId) {
        return evaluationRepository.countByObjectiveId(objectiveId);
    }
    
    @Override
    public EvaluationDTO convertToDTO(Evaluation evaluation) {
        EvaluationDTO dto = new EvaluationDTO();
        dto.setId(evaluation.getId());
        dto.setObjectiveId(evaluation.getObjectiveId());
        dto.setUserId(evaluation.getUserId());
        dto.setRating(evaluation.getRating());
        dto.setFeedback(evaluation.getFeedback());
        dto.setStatus(evaluation.getStatus());
        dto.setCreatedAt(evaluation.getCreatedAt());
        dto.setUpdatedAt(evaluation.getUpdatedAt());
        
        // Load user information
        userRepository.findById(evaluation.getUserId()).ifPresent(user -> {
            dto.setUsername(user.getUsername());
            dto.setUserFullName(user.getFullName());
        });
        
        return dto;
    }
}
