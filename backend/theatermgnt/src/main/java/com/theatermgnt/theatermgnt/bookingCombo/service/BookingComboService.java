package com.theatermgnt.theatermgnt.bookingCombo.service;

import com.theatermgnt.theatermgnt.booking.dto.response.BookingPricingResponse;
import com.theatermgnt.theatermgnt.bookingCombo.dto.request.UpdateBookingCombosRequest;

public interface BookingComboService {
    BookingPricingResponse updateCombos(String bookingId, UpdateBookingCombosRequest request);
}
