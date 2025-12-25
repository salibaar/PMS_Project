package com.gov.pms.repository;

import com.gov.pms.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
    
    Page<Plan> findByYear(Integer year, Pageable pageable);
    
    Page<Plan> findByStatus(Plan.PlanStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Plan p WHERE p.year = ?1 AND p.status = ?2")
    List<Plan> findActivePlansByYear(Integer year, Plan.PlanStatus status);
    
    @Query("SELECT p FROM Plan p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Plan> searchByTitle(String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Plan p WHERE " +
           "(:year IS NULL OR p.year = :year) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:minProgress IS NULL OR p.progress >= :minProgress) AND " +
           "(:maxProgress IS NULL OR p.progress <= :maxProgress)")
    Page<Plan> findByFilters(Integer year, Plan.PlanStatus status, 
                             Integer minProgress, Integer maxProgress, Pageable pageable);
    
    @Query("SELECT p FROM Plan p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Plan> fullTextSearch(String keyword, Pageable pageable);
    
    List<Plan> findByYear(Integer year);
}
