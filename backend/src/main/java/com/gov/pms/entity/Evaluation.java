package com.gov.pms.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evaluations")
@Data
public class Evaluation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "objective_id", nullable = false)
    private UUID objectiveId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_id", insertable = false, updatable = false)
    private Objective objective;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @Column(nullable = false)
    private Integer rating; // 1-5 stars
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EvaluationStatus status = EvaluationStatus.PENDING;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum EvaluationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
    
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
