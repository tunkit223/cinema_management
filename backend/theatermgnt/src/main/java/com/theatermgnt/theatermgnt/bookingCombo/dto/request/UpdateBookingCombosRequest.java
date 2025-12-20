package com.theatermgnt.theatermgnt.bookingCombo.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBookingCombosRequest {
    List<ComboItemRequest> combos;
}
