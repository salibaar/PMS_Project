package com.gov.pms.repository;

import com.gov.pms.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = ?1 AND n.isRead = false")
    Long countUnreadByUserId(UUID userId);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = ?1 AND n.isRead = false")
    void markAllAsReadByUserId(UUID userId);
    
    List<Notification> findByUserIdAndRelatedEntityIdOrderByCreatedAtDesc(UUID userId, UUID relatedEntityId);
}
