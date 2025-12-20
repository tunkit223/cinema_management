package com.theatermgnt.theatermgnt.booking.dto.response;

import com.theatermgnt.theatermgnt.seat.dto.response.SeatResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CreateBookingResponse {
    String id;
    Instant expiredAt;
    BigDecimal subtotal;
    BigDecimal totalAmount;
}
