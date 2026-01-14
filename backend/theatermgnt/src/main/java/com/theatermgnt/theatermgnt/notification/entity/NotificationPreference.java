package com.theatermgnt.theatermgnt.notification.entity;

import com.theatermgnt.theatermgnt.common.entity.BaseEntity;
import com.theatermgnt.theatermgnt.notification.enums.NotificationCategory;
import com.theatermgnt.theatermgnt.notification.enums.NotificationStatus;
import com.theatermgnt.theatermgnt.notification.enums.Priority;
import com.theatermgnt.theatermgnt.notification.enums.RecipientType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_preferences")
@SQLDelete(sql = "UPDATE notification_preferences SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationPreference extends BaseEntity {
    String recipientId;
    @Enumerated(EnumType.STRING)
    RecipientType recipientType;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    NotificationChannel channel;
    
    @Enumerated(EnumType.STRING)
    NotificationCategory category;
    
    Boolean isEnabled;
}
