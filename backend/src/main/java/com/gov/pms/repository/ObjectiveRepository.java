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
    
    @Query("SELECT o FROM Objective o WHERE " +
           "(:planId IS NULL OR o.plan.id = :planId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:minProgress IS NULL OR o.progress >= :minProgress) AND " +
           "(:maxProgress IS NULL OR o.progress <= :maxProgress) AND " +
           "(:isBreakthrough IS NULL OR o.isBreakthrough = :isBreakthrough)")
    List<Objective> findByFilters(UUID planId, Objective.ObjectiveStatus status,
                                   Integer minProgress, Integer maxProgress, 
                                   Boolean isBreakthrough);
    
    @Query("SELECT o FROM Objective o WHERE " +
           "LOWER(o.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Objective> fullTextSearch(String keyword);
}
