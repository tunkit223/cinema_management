package com.theatermgnt.theatermgnt.booking.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingPricingResponse {
    UUID bookingId;
    BigDecimal subTotal;
    BigDecimal discount;
    BigDecimal totalAmount;
}
