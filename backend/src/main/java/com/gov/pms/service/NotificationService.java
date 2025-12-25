package com.gov.pms.service;

import com.gov.pms.dto.NotificationDTO;
import com.gov.pms.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    
    Notification createNotification(UUID userId, String title, String message, 
                                   Notification.NotificationType type, 
                                   UUID relatedEntityId, String relatedEntityType);
    
    Notification markAsRead(UUID notificationId, UUID userId);
    
    void markAllAsRead(UUID userId);
    
    void deleteNotification(UUID notificationId, UUID userId);
    
    Page<Notification> getUserNotifications(UUID userId, Pageable pageable);
    
    List<Notification> getUnreadNotifications(UUID userId);
    
    Long getUnreadCount(UUID userId);
    
    NotificationDTO convertToDTO(Notification notification);
}
