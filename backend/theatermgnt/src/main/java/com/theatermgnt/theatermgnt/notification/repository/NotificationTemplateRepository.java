package com.theatermgnt.theatermgnt.notification.repository;

import com.theatermgnt.theatermgnt.notification.entity.Notification;
import com.theatermgnt.theatermgnt.notification.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {
}
