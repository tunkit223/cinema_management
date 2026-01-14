package com.theatermgnt.theatermgnt.notification.repository;

import com.theatermgnt.theatermgnt.notification.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, String> {
}
