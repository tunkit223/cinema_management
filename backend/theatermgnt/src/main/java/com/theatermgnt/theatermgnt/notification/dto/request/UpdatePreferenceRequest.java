package com.theatermgnt.theatermgnt.notification.dto.request;

import com.theatermgnt.theatermgnt.notification.enums.NotificationCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePreferenceRequest {
    @NotBlank(message = "Recipient ID is required")
    String recipientId;
    
    @NotBlank(message = "Channel name is required")
    String channelName;
    
    @NotNull(message = "Category is required")
    NotificationCategory category;
    
    @NotNull(message = "Enabled status is required")
    Boolean isEnabled;
}
