package com.theatermgnt.theatermgnt.notification.mapper;

import com.theatermgnt.theatermgnt.notification.dto.request.NotificationTemplateRequest;
import com.theatermgnt.theatermgnt.notification.dto.response.NotificationTemplateResponse;
import com.theatermgnt.theatermgnt.notification.entity.NotificationTemplate;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationTemplateMapper {
    
    NotificationTemplate toEntity(NotificationTemplateRequest request);
    
    NotificationTemplateResponse toResponse(NotificationTemplate entity);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget NotificationTemplate entity, NotificationTemplateRequest request);
}
