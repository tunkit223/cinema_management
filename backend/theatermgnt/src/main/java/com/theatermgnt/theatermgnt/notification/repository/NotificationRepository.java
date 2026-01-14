package com.theatermgnt.theatermgnt.notification.repository;

import com.theatermgnt.theatermgnt.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
}
