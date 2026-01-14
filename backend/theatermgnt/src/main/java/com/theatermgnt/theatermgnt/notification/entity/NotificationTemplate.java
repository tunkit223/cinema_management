package com.theatermgnt.theatermgnt.notification.entity;

import com.theatermgnt.theatermgnt.common.entity.BaseEntity;
import com.theatermgnt.theatermgnt.notification.enums.Priority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_templates")
@SQLDelete(sql = "UPDATE notification_templates SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationTemplate extends BaseEntity {
    String templateCode;
    String titleTemplate;

    @Column(columnDefinition = "TEXT")
    String contentTemplate;
    Priority priority;
}
