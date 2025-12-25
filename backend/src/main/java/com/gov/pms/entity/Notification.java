package com.gov.pms.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Column(nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "related_entity_id")
    private UUID relatedEntityId;
    
    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum NotificationType {
        COMMENT,            // New comment on objective
        EVALUATION,         // New evaluation/rating
        PLAN_STATUS_CHANGE, // Plan status changed
        OBJECTIVE_ASSIGNED, // Objective assigned to user
        DEADLINE_APPROACHING, // Deadline coming soon
        PROGRESS_UPDATE,    // Progress updated
        MENTION             // User mentioned in comment
    }
}
