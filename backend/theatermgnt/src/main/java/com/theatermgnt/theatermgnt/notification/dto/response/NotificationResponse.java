package com.theatermgnt.theatermgnt.notification.dto.response;

import com.theatermgnt.theatermgnt.notification.enums.NotificationCategory;
import com.theatermgnt.theatermgnt.notification.enums.NotificationStatus;
import com.theatermgnt.theatermgnt.notification.enums.Priority;
import com.theatermgnt.theatermgnt.notification.enums.RecipientType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String id;
    String templateCode;
    String recipientId;
    RecipientType recipientType;
    NotificationCategory category;
    Priority priority;
    NotificationStatus status;
    String title;
    String content;
    Boolean isRead;
    LocalDateTime readAt;
    LocalDateTime createdAt;
}
