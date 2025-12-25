package com.gov.pms.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "key_results")
@Data
public class KeyResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_id", nullable = false)
    private Objective objective;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "target_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetValue;
    
    @Column(name = "current_value", precision = 10, scale = 2)
    private BigDecimal currentValue = BigDecimal.ZERO;
    
    @Column(length = 50)
    private String unit;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeyResultStatus status = KeyResultStatus.NOT_STARTED;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum KeyResultStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
    
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
