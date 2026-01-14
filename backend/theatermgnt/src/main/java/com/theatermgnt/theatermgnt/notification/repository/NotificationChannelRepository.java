package com.theatermgnt.theatermgnt.notification.repository;

import com.theatermgnt.theatermgnt.notification.entity.Notification;
import com.theatermgnt.theatermgnt.notification.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, String> {
}
