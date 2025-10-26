package com.theatermgnt.theatermgnt.authorization.mapper;

import com.theatermgnt.theatermgnt.authorization.dto.request.PermissionRequest;
import com.theatermgnt.theatermgnt.authorization.dto.response.PermissionResponse;
import com.theatermgnt.theatermgnt.authorization.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
