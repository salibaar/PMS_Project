package com.gov.pms.repository;

import com.gov.pms.entity.KeyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KeyResultRepository extends JpaRepository<KeyResult, UUID> {
    
    List<KeyResult> findByObjectiveId(UUID objectiveId);
    
    List<KeyResult> findByStatus(KeyResult.KeyResultStatus status);
}
