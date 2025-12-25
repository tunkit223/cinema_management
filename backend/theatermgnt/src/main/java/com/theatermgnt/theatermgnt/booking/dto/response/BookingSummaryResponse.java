package com.theatermgnt.theatermgnt.booking.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;
import com.theatermgnt.theatermgnt.bookingCombo.dto.response.ComboSummaryResponse;
import com.theatermgnt.theatermgnt.seat.dto.response.SeatResponse;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingSummaryResponse {
    String bookingId;
    BookingStatus status;
    Instant expiredAt;

    List<SeatResponse> seats;
    List<ComboSummaryResponse> combos;

    BigDecimal seatSubtotal;
    BigDecimal comboSubtotal;
    BigDecimal subTotal;
    BigDecimal discount;
    BigDecimal totalAmount;
}
