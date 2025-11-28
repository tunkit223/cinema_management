package com.theatermgnt.theatermgnt.equipment.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentCategoryResponse {
    String id;
    String name;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
