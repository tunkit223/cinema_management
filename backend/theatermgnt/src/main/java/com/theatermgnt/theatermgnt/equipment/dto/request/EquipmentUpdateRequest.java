package com.theatermgnt.theatermgnt.equipment.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentUpdateRequest {
    String name;
    String categoryId;
    String roomId;
    String serialNumber;
    String status;
    LocalDate purchaseDate;
}
