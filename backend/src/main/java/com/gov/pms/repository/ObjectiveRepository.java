package com.gov.pms.repository;

import com.gov.pms.entity.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, UUID> {
    
    List<Objective> findByPlanIdAndParentIsNull(UUID planId);
    
    List<Objective> findByParentId(UUID parentId);
    
    List<Objective> findByPlanIdAndIsBreakthroughTrue(UUID planId);
    
    @Query("SELECT o FROM Objective o LEFT JOIN FETCH o.keyResults WHERE o.id = ?1")
    Objective findByIdWithKeyResults(UUID id);
}
