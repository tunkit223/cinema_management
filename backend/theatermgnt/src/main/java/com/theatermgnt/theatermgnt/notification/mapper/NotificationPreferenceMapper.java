package com.theatermgnt.theatermgnt.notification.mapper;

import com.theatermgnt.theatermgnt.notification.dto.request.NotificationPreferenceRequest;
import com.theatermgnt.theatermgnt.notification.dto.response.NotificationPreferenceResponse;
import com.theatermgnt.theatermgnt.notification.entity.NotificationChannel;
import com.theatermgnt.theatermgnt.notification.entity.NotificationPreference;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationPreferenceMapper {
    
    @Mapping(target = "channel", source = "channelId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    NotificationPreference toEntity(NotificationPreferenceRequest request, @Context NotificationChannel channel);
    
    @Mapping(source = "channel.id", target = "channelId")
    @Mapping(source = "channel.name", target = "channelName")
    NotificationPreferenceResponse toResponse(NotificationPreference entity);
}
