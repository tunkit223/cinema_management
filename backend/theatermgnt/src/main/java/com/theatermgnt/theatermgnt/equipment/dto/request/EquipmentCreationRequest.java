package com.theatermgnt.theatermgnt.equipment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentCreationRequest {
    @NotBlank(message = "Equipment name is required")
    String name;
    
    @NotBlank(message = "Category ID is required")
    String categoryId;
    
    @NotBlank(message = "Room ID is required")
    String roomId;
    
    String serialNumber;
    
    @NotBlank(message = "Status is required")
    String status;
    
    LocalDate purchaseDate;
}
