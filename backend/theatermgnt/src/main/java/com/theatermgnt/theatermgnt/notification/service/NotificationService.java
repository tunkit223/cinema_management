package com.theatermgnt.theatermgnt.notification.service;

import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
import com.theatermgnt.theatermgnt.notification.dto.request.CreateNotificationRequest;
import com.theatermgnt.theatermgnt.notification.dto.response.NotificationDetailResponse;
import com.theatermgnt.theatermgnt.notification.dto.response.NotificationLogResponse;
import com.theatermgnt.theatermgnt.notification.dto.response.NotificationResponse;
import com.theatermgnt.theatermgnt.notification.entity.Notification;
import com.theatermgnt.theatermgnt.notification.entity.NotificationLog;
import com.theatermgnt.theatermgnt.notification.entity.NotificationTemplate;
import com.theatermgnt.theatermgnt.notification.enums.NotificationCategory;
import com.theatermgnt.theatermgnt.notification.enums.NotificationStatus;
import com.theatermgnt.theatermgnt.notification.mapper.NotificationMapper;
import com.theatermgnt.theatermgnt.notification.repository.NotificationLogRepository;
import com.theatermgnt.theatermgnt.notification.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NotificationService - Core service for managing notifications
 * This is the main entry point for creating and managing notifications
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationLogRepository logRepository;
    NotificationTemplateService templateService;
    NotificationDispatcher dispatcher;
    NotificationMapper notificationMapper;
    
    /**
     * Create notification and dispatch to channels
     * This is the main method called by event listeners
     */
    @Transactional
    public Notification createAndSend(CreateNotificationRequest request) {
        log.info("Creating and sending notification for recipient: {}, template: {}", 
                request.getRecipientId(), request.getTemplateCode());
        
        // 1. Get template
        NotificationTemplate template = templateService.getTemplateByCode(request.getTemplateCode());
        
        // 2. Build metadata with template variables and category
        Map<String, Object> metadata = new HashMap<>();
        if (request.getMetadata() != null) {
            metadata.putAll(request.getMetadata());
        }
        metadata.put("category", request.getCategory().name());
        
        // Render and store title/content in metadata for quick access
        String title = templateService.renderTitle(request.getTemplateCode(), request.getMetadata());
        String content = templateService.renderTemplate(request.getTemplateCode(), request.getMetadata());
        metadata.put("title", title);
        metadata.put("content", content);
        
        // 3. Create notification entity using builder
        Notification notification = Notification.builder()
                .notificationTemplate(template)
                .recipientId(request.getRecipientId())
                .recipientType(request.getRecipientType())
                .priority(request.getPriority() != null ? request.getPriority() : template.getPriority())
                .status(NotificationStatus.PENDING)
                .metadata(metadata)
                .build();
        
        // 4. Save notification to DB
        Notification saved = notificationRepository.save(notification);
        log.info("Notification created with ID: {}", saved.getId());
        
        // 5. Dispatch to channels asynchronously
        dispatcher.dispatch(saved, request.getChannels(), metadata);
        
        return saved;
    }
    
    /**
     * Get user's notifications with pagination
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(String userId, Pageable pageable) {
        log.debug("Getting notifications for user: {}", userId);
        
        Page<Notification> notifications = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId, pageable);
        
        return notifications.map(this::toNotificationResponse);
    }
    
    /**
     * Get unread notification count for a user
     */
    @Transactional(readOnly = true)
    public Long getUnreadCount(String userId) {
        return notificationRepository.countByRecipientIdAndReadAtIsNull(userId);
    }
    
    /**
     * Mark a notification as read
     */
    @Transactional
    public NotificationResponse markAsRead(String notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        
        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Notification marked as read: {}", notificationId);
        }
        
        return toNotificationResponse(notification);
    }
    
    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(String userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        
        List<Notification> unreadNotifications = notificationRepository
                .findByRecipientIdAndReadAtIsNull(userId);
        
        LocalDateTime now = LocalDateTime.now();
        unreadNotifications.forEach(notification -> notification.setReadAt(now));
        
        notificationRepository.saveAll(unreadNotifications);
        log.info("Marked {} notifications as read for user: {}", 
                unreadNotifications.size(), userId);
    }
    
    /**
     * Get notification detail with logs
     */
    @Transactional(readOnly = true)
    public NotificationDetailResponse getNotificationDetail(String notificationId) {
        log.debug("Getting notification detail: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        
        List<NotificationLog> logs = logRepository.findByNotificationOrderBySentAtDesc(notification);
        
        return toNotificationDetailResponse(notification, logs);
    }
    
    /**
     * Get all notifications (Admin API)
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(this::toNotificationResponse);
    }
    
    /**
     * Delete notification (soft delete)
     */
    @Transactional
    public void deleteNotification(String notificationId) {
        log.info("Deleting notification: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        
        notificationRepository.delete(notification);
        log.info("Notification deleted: {}", notificationId);
    }
    
    /**
     * Private helper methods to enrich mapper responses
     */
    private NotificationResponse toNotificationResponse(Notification notification) {
        NotificationResponse response = notificationMapper.toResponse(notification);
        enrichNotificationResponse(response, notification);
        return response;
    }
    
    private NotificationDetailResponse toNotificationDetailResponse(Notification notification, List<NotificationLog> logs) {
        NotificationDetailResponse response = notificationMapper.toDetailResponse(notification);
        enrichNotificationResponse(response, notification);
        
        // Map logs
        List<NotificationLogResponse> logResponses = logs.stream()
                .map(notificationMapper::toLogResponse)
                .collect(Collectors.toList());
        response.setLogs(logResponses);
        
        return response;
    }
    
    private void enrichNotificationResponse(NotificationResponse response, Notification notification) {
        // Set category from metadata
        if (notification.getMetadata() != null && notification.getMetadata().containsKey("category")) {
            response.setCategory(NotificationCategory.valueOf((String) notification.getMetadata().get("category")));
        }
        
        // Set title from metadata
        if (notification.getMetadata() != null && notification.getMetadata().containsKey("title")) {
            response.setTitle((String) notification.getMetadata().get("title"));
        }
        
        // Set content from metadata
        if (notification.getMetadata() != null && notification.getMetadata().containsKey("content")) {
            response.setContent((String) notification.getMetadata().get("content"));
        }
        
        // Set isRead
        response.setIsRead(notification.getReadAt() != null);
    }
}
