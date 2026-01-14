package com.theatermgnt.theatermgnt.notification.repository;

import com.theatermgnt.theatermgnt.notification.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, String> {
    Optional<NotificationChannel> findByName(String name);
    
    List<NotificationChannel> findByIsActive(Boolean isActive);
}
