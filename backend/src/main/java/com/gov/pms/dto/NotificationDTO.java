package com.gov.pms.dto;

import com.gov.pms.entity.Notification;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDTO {
    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Boolean isRead;
    private UUID relatedEntityId;
    private String relatedEntityType;
    private LocalDateTime createdAt;
}
