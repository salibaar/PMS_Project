package com.gov.pms.controller;

import com.gov.pms.dto.CommentDTO;
import com.gov.pms.entity.Comment;
import com.gov.pms.security.CustomUserDetailsService;
import com.gov.pms.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/comments")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Comments", description = "API quản lý bình luận (Comments)")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Operation(summary = "Tạo bình luận mới", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @Valid @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        var user = customUserDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        Comment comment = commentService.createComment(commentDTO, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.convertToDTO(comment));
    }
    
    @Operation(summary = "Cập nhật bình luận", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @Parameter(description = "ID bình luận") @PathVariable UUID id,
            @Valid @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        var user = customUserDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        Comment comment = commentService.updateComment(id, commentDTO, user.getId());
        return ResponseEntity.ok(commentService.convertToDTO(comment));
    }
    
    @Operation(summary = "Xóa bình luận", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID bình luận") @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        var user = customUserDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        commentService.deleteComment(id, user.getId());
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Lấy bình luận theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(
            @Parameter(description = "ID bình luận") @PathVariable UUID id) {
        
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(commentService.convertToDTO(comment));
    }
    
    @Operation(summary = "Lấy bình luận theo objective (có phân trang)")
    @GetMapping("/objective/{objectiveId}")
    public ResponseEntity<Map<String, Object>> getCommentsByObjectiveId(
            @Parameter(description = "ID nhiệm vụ") @PathVariable UUID objectiveId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentService.getCommentsByObjectiveId(objectiveId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("comments", commentPage.getContent().stream()
            .map(commentService::convertToDTOWithReplies)
            .collect(Collectors.toList()));
        response.put("currentPage", commentPage.getNumber());
        response.put("totalItems", commentPage.getTotalElements());
        response.put("totalPages", commentPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lấy tất cả bình luận gốc theo objective")
    @GetMapping("/objective/{objectiveId}/root")
    public ResponseEntity<List<CommentDTO>> getRootComments(
            @Parameter(description = "ID nhiệm vụ") @PathVariable UUID objectiveId) {
        
        List<Comment> comments = commentService.getRootCommentsByObjectiveId(objectiveId);
        List<CommentDTO> dtos = comments.stream()
            .map(commentService::convertToDTOWithReplies)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @Operation(summary = "Lấy replies của một bình luận")
    @GetMapping("/{id}/replies")
    public ResponseEntity<List<CommentDTO>> getReplies(
            @Parameter(description = "ID bình luận cha") @PathVariable UUID id) {
        
        List<Comment> replies = commentService.getRepliesByParentId(id);
        List<CommentDTO> dtos = replies.stream()
            .map(commentService::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @Operation(summary = "Đếm số lượng bình luận theo objective")
    @GetMapping("/objective/{objectiveId}/count")
    public ResponseEntity<Map<String, Long>> countComments(
            @Parameter(description = "ID nhiệm vụ") @PathVariable UUID objectiveId) {
        
        long count = commentService.countCommentsByObjectiveId(objectiveId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
}
