package com.gov.pms.entity;

import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "objectives")
@Data
@SQLDelete(sql = "UPDATE objectives SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Objective {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Objective parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Objective> children = new ArrayList<>();
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "is_breakthrough", nullable = false)
    private Boolean isBreakthrough = false;
    
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjectiveStatus status = ObjectiveStatus.NOT_STARTED;
    
    @Column(nullable = false)
    private Integer progress = 0;
    
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @OneToMany(mappedBy = "objective", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeyResult> keyResults = new ArrayList<>();
    
    public enum ObjectiveStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED, BLOCKED
    }
    
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
