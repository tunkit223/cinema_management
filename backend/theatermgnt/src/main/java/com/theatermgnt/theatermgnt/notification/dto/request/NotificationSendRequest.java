package com.theatermgnt.theatermgnt.notification.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

/**
 * Request DTO for sending notifications through any channel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationSendRequest {
    String recipientId;
    String recipientEmail;
    String recipientPhone;
    String recipientName;
    
    String templateCode;
    String title;
    String content;
    
    Map<String, Object> metadata;
    Map<String, Object> channelConfig;
}
