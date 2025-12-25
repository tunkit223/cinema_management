package com.theatermgnt.theatermgnt.booking.service;

import java.util.UUID;

import com.theatermgnt.theatermgnt.booking.dto.request.CreateBookingRequest;
import com.theatermgnt.theatermgnt.booking.dto.request.DiscountPointRequest;
import com.theatermgnt.theatermgnt.booking.dto.response.BookingSummaryResponse;
import com.theatermgnt.theatermgnt.booking.dto.response.CreateBookingResponse;

public interface BookingService {
    CreateBookingResponse createBooking(CreateBookingRequest request);

    BookingSummaryResponse getBookingSummary(UUID bookingId);

    BookingSummaryResponse redeemPoints(UUID bookingId, DiscountPointRequest pointsToRedeem);
}
