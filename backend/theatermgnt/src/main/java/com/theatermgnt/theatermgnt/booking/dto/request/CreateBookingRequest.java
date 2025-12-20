package com.theatermgnt.theatermgnt.booking.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookingRequest {
    String customerId;
    String screeningId;
    List<String> screeningSeatIds;
}
