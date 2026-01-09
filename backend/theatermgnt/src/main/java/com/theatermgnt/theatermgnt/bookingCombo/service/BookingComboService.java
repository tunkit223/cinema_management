package com.theatermgnt.theatermgnt.bookingCombo.service;

import java.util.List;
import java.util.UUID;

import com.theatermgnt.theatermgnt.booking.dto.response.BookingPricingResponse;
import com.theatermgnt.theatermgnt.bookingCombo.dto.request.UpdateBookingCombosRequest;
import com.theatermgnt.theatermgnt.bookingCombo.dto.response.ComboSummaryResponse;

public interface BookingComboService {
    BookingPricingResponse updateCombos(UUID bookingId, UpdateBookingCombosRequest request);
    List<ComboSummaryResponse> getCombos(UUID bookingId);

}
