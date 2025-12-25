package com.gov.pms.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CommentDTO {
    
    private UUID id;
    
    @NotNull(message = "Objective ID không được để trống")
    private UUID objectiveId;
    
    private UUID userId;
    
    private String username;
    
    private String userFullName;
    
    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(min = 1, max = 5000, message = "Nội dung phải từ 1 đến 5000 ký tự")
    private String content;
    
    private UUID parentId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // For nested comments
    private List<CommentDTO> replies;
}
