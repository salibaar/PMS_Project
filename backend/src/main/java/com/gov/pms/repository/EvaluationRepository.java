package com.gov.pms.repository;

import com.gov.pms.entity.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {
    
    Page<Evaluation> findByObjectiveId(UUID objectiveId, Pageable pageable);
    
    List<Evaluation> findByObjectiveId(UUID objectiveId);
    
    Page<Evaluation> findByUserId(UUID userId, Pageable pageable);
    
    Page<Evaluation> findByStatus(Evaluation.EvaluationStatus status, Pageable pageable);
    
    Optional<Evaluation> findByObjectiveIdAndUserId(UUID objectiveId, UUID userId);
    
    @Query("SELECT AVG(e.rating) FROM Evaluation e WHERE e.objectiveId = :objectiveId AND e.status = 'APPROVED'")
    Double getAverageRatingByObjectiveId(UUID objectiveId);
    
    @Query("SELECT COUNT(e) FROM Evaluation e WHERE e.objectiveId = :objectiveId")
    long countByObjectiveId(UUID objectiveId);
}
