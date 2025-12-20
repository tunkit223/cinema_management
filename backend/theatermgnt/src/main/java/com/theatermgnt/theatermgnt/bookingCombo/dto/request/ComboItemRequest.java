package com.theatermgnt.theatermgnt.bookingCombo.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboItemRequest {
    String comboId;
    int quantity;
}
