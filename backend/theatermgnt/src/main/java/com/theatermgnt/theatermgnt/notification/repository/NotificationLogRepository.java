package com.theatermgnt.theatermgnt.notification.repository;

import com.theatermgnt.theatermgnt.notification.entity.Notification;
import com.theatermgnt.theatermgnt.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {
    List<NotificationLog> findByNotificationOrderBySentAtDesc(Notification notification);
}
