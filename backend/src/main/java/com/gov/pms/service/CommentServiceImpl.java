package com.gov.pms.service;

import com.gov.pms.dto.CommentDTO;
import com.gov.pms.entity.Comment;
import com.gov.pms.entity.User;
import com.gov.pms.exception.ResourceNotFoundException;
import com.gov.pms.repository.CommentRepository;
import com.gov.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    public Comment createComment(CommentDTO commentDTO, UUID userId) {
        Comment comment = new Comment();
        comment.setObjectiveId(commentDTO.getObjectiveId());
        comment.setUserId(userId);
        comment.setContent(commentDTO.getContent());
        comment.setParentId(commentDTO.getParentId());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        Comment savedComment = commentRepository.save(comment);
        
        // Create notification for relevant users
        // TODO: Notify objective owner or parent comment author
        try {
            User user = userRepository.findById(userId).orElse(null);
            String username = user != null ? user.getUsername() : "User";
            notificationService.createNotification(
                userId, 
                "New Comment",
                username + " added a comment on an objective",
                com.gov.pms.entity.Notification.NotificationType.COMMENT,
                commentDTO.getObjectiveId(),
                "OBJECTIVE"
            );
        } catch (Exception e) {
            // Don't fail comment creation if notification fails
        }
        
        return savedComment;
    }
    
    @Override
    public Comment updateComment(UUID id, CommentDTO commentDTO, UUID userId) {
        Comment comment = getCommentById(id);
        
        // Only the author can update their comment
        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật bình luận này");
        }
        
        comment.setContent(commentDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }
    
    @Override
    public void deleteComment(UUID id, UUID userId) {
        Comment comment = getCommentById(id);
        
        // Only the author can delete their comment
        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền xóa bình luận này");
        }
        
        commentRepository.delete(comment); // Soft delete
    }
    
    @Override
    @Transactional(readOnly = true)
    public Comment getCommentById(UUID id) {
        return commentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByObjectiveId(UUID objectiveId, Pageable pageable) {
        return commentRepository.findByObjectiveIdAndParentIdIsNull(objectiveId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Comment> getRootCommentsByObjectiveId(UUID objectiveId) {
        return commentRepository.findByObjectiveIdAndParentIdIsNull(objectiveId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Comment> getRepliesByParentId(UUID parentId) {
        return commentRepository.findByParentId(parentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countCommentsByObjectiveId(UUID objectiveId) {
        return commentRepository.countByObjectiveId(objectiveId);
    }
    
    @Override
    public CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setObjectiveId(comment.getObjectiveId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setParentId(comment.getParentId());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        
        // Load user information
        userRepository.findById(comment.getUserId()).ifPresent(user -> {
            dto.setUsername(user.getUsername());
            dto.setUserFullName(user.getFullName());
        });
        
        return dto;
    }
    
    @Override
    public CommentDTO convertToDTOWithReplies(Comment comment) {
        CommentDTO dto = convertToDTO(comment);
        
        // Load replies recursively
        List<Comment> replies = getRepliesByParentId(comment.getId());
        if (!replies.isEmpty()) {
            dto.setReplies(replies.stream()
                .map(this::convertToDTOWithReplies)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
}
