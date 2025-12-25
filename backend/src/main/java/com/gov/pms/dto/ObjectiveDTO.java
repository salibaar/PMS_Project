package com.gov.pms.dto;

import com.gov.pms.entity.Objective;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ObjectiveDTO {
    
    private UUID id;
    
    @NotNull(message = "Plan ID không được để trống")
    private UUID planId;
    
    private UUID parentId;
    
    @NotBlank(message = "Nội dung không được để trống")
    @Size(min = 10, max = 500, message = "Nội dung phải từ 10 đến 500 ký tự")
    private String content;
    
    @NotNull(message = "Trạng thái đột phá không được để trống")
    private Boolean isBreakthrough;
    
    private Integer orderIndex = 0;
    
    private Objective.ObjectiveStatus status;
    
    @Min(value = 0, message = "Tiến độ phải từ 0")
    @Max(value = 100, message = "Tiến độ tối đa 100")
    private Integer progress = 0;
    
    private UUID createdBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private List<KeyResultDTO> keyResults = new ArrayList<>();
    
    private List<ObjectiveDTO> children = new ArrayList<>();
}
