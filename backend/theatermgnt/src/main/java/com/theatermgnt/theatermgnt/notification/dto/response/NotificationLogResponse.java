package com.theatermgnt.theatermgnt.notification.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationLogResponse {
    String id;
    String notificationId;
    String channelName;
    String status;
    Map<String, Object> providerResponse;
    LocalDateTime sentAt;
}
