package com.theatermgnt.theatermgnt.equipment.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentResponse {
    String id;
    String name;
    String categoryId;
    String roomId;
    String serialNumber;
    String status;
    LocalDate purchaseDate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
