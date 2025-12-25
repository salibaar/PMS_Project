package com.gov.pms.dto;

import com.gov.pms.entity.KeyResult;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class KeyResultDTO {
    
    private UUID id;
    
    @NotNull(message = "Objective ID không được để trống")
    private UUID objectiveId;
    
    @NotBlank(message = "Mô tả không được để trống")
    @Size(min = 5, max = 300, message = "Mô tả phải từ 5 đến 300 ký tự")
    private String description;
    
    @NotNull(message = "Giá trị mục tiêu không được để trống")
    @DecimalMin(value = "0.0", message = "Giá trị mục tiêu phải >= 0")
    private BigDecimal targetValue;
    
    @DecimalMin(value = "0.0", message = "Giá trị hiện tại phải >= 0")
    private BigDecimal currentValue = BigDecimal.ZERO;
    
    @Size(max = 50, message = "Đơn vị tối đa 50 ký tự")
    private String unit;
    
    private KeyResult.KeyResultStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
