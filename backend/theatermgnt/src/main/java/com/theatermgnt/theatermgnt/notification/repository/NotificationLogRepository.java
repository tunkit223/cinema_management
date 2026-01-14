package com.theatermgnt.theatermgnt.notification.repository;

import com.theatermgnt.theatermgnt.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {
}
