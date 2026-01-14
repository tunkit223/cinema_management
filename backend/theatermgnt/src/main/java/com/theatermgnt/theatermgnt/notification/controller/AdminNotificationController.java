package com.theatermgnt.theatermgnt.notification.controller;

import com.theatermgnt.theatermgnt.common.dto.response.ApiResponse;
import com.theatermgnt.theatermgnt.notification.dto.request.CreateNotificationRequest;
import com.theatermgnt.theatermgnt.notification.dto.response.NotificationDetailResponse;
import com.theatermgnt.theatermgnt.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AdminNotificationController - Admin APIs for notification management
 * Allows admins to view all notifications and manually send notifications
 */
@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {
    NotificationService notificationService;

    /**
     * Get all notifications (Admin view)
     * GET /admin/notifications?page=0&size=20
     */
    @GetMapping
    public ApiResponse<Page<NotificationDetailResponse>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Admin getting all notifications - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        
        return ApiResponse.<Page<NotificationDetailResponse>>builder()
                .result(notificationService.getAllNotifications(pageable))
                .build();
    }

    /**
     * Manually send a notification
     * POST /admin/notifications/send
     */
    @PostMapping("/send")
    public ApiResponse<NotificationDetailResponse> sendNotification(
            @RequestBody @Valid CreateNotificationRequest request) {
        log.info("Admin manually sending notification to recipient: {}", request.getRecipientId());
        
        return ApiResponse.<NotificationDetailResponse>builder()
                .result(notificationService.createAndSend(request))
                .build();
    }

    /**
     * Get notification detail by ID (Admin access)
     * GET /admin/notifications/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<NotificationDetailResponse> getNotificationDetail(@PathVariable String id) {
        log.info("Admin getting notification detail: {}", id);
        
        return ApiResponse.<NotificationDetailResponse>builder()
                .result(notificationService.getNotificationDetail(id))
                .build();
    }

    /**
     * Delete any notification (Admin only)
     * DELETE /admin/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(@PathVariable String id) {
        log.info("Admin deleting notification: {}", id);
        
        notificationService.deleteNotification(id);
        
        return ApiResponse.<Void>builder()
                .message("Notification deleted successfully")
                .build();
    }
}
