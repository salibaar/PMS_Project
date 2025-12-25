package com.gov.pms.repository;

import com.gov.pms.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    
    Page<Comment> findByObjectiveIdAndParentIdIsNull(UUID objectiveId, Pageable pageable);
    
    List<Comment> findByObjectiveIdAndParentIdIsNull(UUID objectiveId);
    
    List<Comment> findByParentId(UUID parentId);
    
    long countByObjectiveId(UUID objectiveId);
    
    @Query("SELECT c FROM Comment c WHERE c.userId = :userId ORDER BY c.createdAt DESC")
    Page<Comment> findByUserId(UUID userId, Pageable pageable);
}
