package com.gov.pms.dto;

import com.gov.pms.entity.Plan;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class PlanDTO {
    
    private UUID id;
    
    @NotNull(message = "Năm không được để trống")
    @Min(value = 2020, message = "Năm phải từ 2020 trở lên")
    @Max(value = 2100, message = "Năm phải nhỏ hơn 2100")
    private Integer year;
    
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
    private String title;
    
    @Size(max = 5000, message = "Mô tả tối đa 5000 ký tự")
    private String description;
    
    private Plan.PlanStatus status;
    
    private UUID createdBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private List<ObjectiveDTO> objectives = new ArrayList<>();
}
