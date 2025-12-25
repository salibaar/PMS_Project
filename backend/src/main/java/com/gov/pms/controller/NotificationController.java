package com.gov.pms.controller;

import com.gov.pms.dto.NotificationDTO;
import com.gov.pms.entity.Notification;
import com.gov.pms.security.JwtTokenProvider;
import com.gov.pms.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "API quản lý thông báo")
@SecurityRequirement(name = "bearer-token")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @GetMapping
    @Operation(summary = "Lấy danh sách thông báo", 
               description = "Lấy tất cả thông báo của user hiện tại với phân trang")
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            HttpServletRequest request,
            Pageable pageable) {
        
        UUID userId = getUserIdFromRequest(request);
        Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
        Page<NotificationDTO> dtos = notifications.map(notificationService::convertToDTO);
        
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/unread")
    @Operation(summary = "Lấy thông báo chưa đọc",
               description = "Lấy tất cả thông báo chưa đọc của user")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(HttpServletRequest request) {
        UUID userId = getUserIdFromRequest(request);
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        List<NotificationDTO> dtos = notifications.stream()
                .map(notificationService::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/unread/count")
    @Operation(summary = "Đếm số thông báo chưa đọc",
               description = "Trả về số lượng thông báo chưa đọc của user")
    public ResponseEntity<Map<String, Long>> getUnreadCount(HttpServletRequest request) {
        UUID userId = getUserIdFromRequest(request);
        Long count = notificationService.getUnreadCount(userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/read")
    @Operation(summary = "Đánh dấu thông báo đã đọc",
               description = "Đánh dấu một thông báo cụ thể là đã đọc")
    public ResponseEntity<NotificationDTO> markAsRead(
            @Parameter(description = "ID của Notification") @PathVariable UUID id,
            HttpServletRequest request) {
        
        UUID userId = getUserIdFromRequest(request);
        Notification notification = notificationService.markAsRead(id, userId);
        NotificationDTO dto = notificationService.convertToDTO(notification);
        
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/read-all")
    @Operation(summary = "Đánh dấu tất cả đã đọc",
               description = "Đánh dấu tất cả thông báo của user là đã đọc")
    public ResponseEntity<Map<String, String>> markAllAsRead(HttpServletRequest request) {
        UUID userId = getUserIdFromRequest(request);
        notificationService.markAllAsRead(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thông báo",
               description = "Xóa một thông báo cụ thể")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @Parameter(description = "ID của Notification") @PathVariable UUID id,
            HttpServletRequest request) {
        
        UUID userId = getUserIdFromRequest(request);
        notificationService.deleteNotification(id, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification deleted successfully");
        
        return ResponseEntity.ok(response);
    }
    
    private UUID getUserIdFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            return UUID.fromString(username); // Assuming username is userId
        }
        throw new IllegalArgumentException("Invalid or missing authentication token");
    }
}
