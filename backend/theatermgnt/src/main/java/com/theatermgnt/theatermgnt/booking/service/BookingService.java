package com.theatermgnt.theatermgnt.booking.service;

import com.theatermgnt.theatermgnt.booking.dto.request.CreateBookingRequest;
import com.theatermgnt.theatermgnt.booking.dto.response.BookingSummaryResponse;
import com.theatermgnt.theatermgnt.booking.dto.response.CreateBookingResponse;

public interface BookingService {
    CreateBookingResponse createBooking(CreateBookingRequest request);
    BookingSummaryResponse getBookingSummary(String bookingId);
}
