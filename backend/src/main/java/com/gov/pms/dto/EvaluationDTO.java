package com.gov.pms.dto;

import com.gov.pms.entity.Evaluation;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EvaluationDTO {
    
    private UUID id;
    
    @NotNull(message = "Objective ID không được để trống")
    private UUID objectiveId;
    
    private UUID userId;
    
    private String username;
    
    private String userFullName;
    
    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating tối thiểu là 1")
    @Max(value = 5, message = "Rating tối đa là 5")
    private Integer rating;
    
    @Size(max = 5000, message = "Feedback tối đa 5000 ký tự")
    private String feedback;
    
    private Evaluation.EvaluationStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
