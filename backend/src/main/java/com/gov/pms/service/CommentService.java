package com.gov.pms.service;

import com.gov.pms.dto.CommentDTO;
import com.gov.pms.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    
    Comment createComment(CommentDTO commentDTO, UUID userId);
    
    Comment updateComment(UUID id, CommentDTO commentDTO, UUID userId);
    
    void deleteComment(UUID id, UUID userId);
    
    Comment getCommentById(UUID id);
    
    Page<Comment> getCommentsByObjectiveId(UUID objectiveId, Pageable pageable);
    
    List<Comment> getRootCommentsByObjectiveId(UUID objectiveId);
    
    List<Comment> getRepliesByParentId(UUID parentId);
    
    long countCommentsByObjectiveId(UUID objectiveId);
    
    CommentDTO convertToDTO(Comment comment);
    
    CommentDTO convertToDTOWithReplies(Comment comment);
}
