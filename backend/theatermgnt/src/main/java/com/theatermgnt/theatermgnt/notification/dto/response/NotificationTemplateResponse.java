package com.theatermgnt.theatermgnt.notification.dto.response;

import com.theatermgnt.theatermgnt.notification.enums.Priority;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationTemplateResponse {
    String id;
    String templateCode;
    String titleTemplate;
    String contentTemplate;
    Priority priority;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
