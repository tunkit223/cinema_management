package com.theatermgnt.theatermgnt.booking.service;

import java.util.UUID;

import com.theatermgnt.theatermgnt.booking.dto.request.CreateBookingRequest;
import com.theatermgnt.theatermgnt.booking.dto.request.DiscountPointRequest;
import com.theatermgnt.theatermgnt.booking.dto.response.BookingSummaryResponse;
import com.theatermgnt.theatermgnt.booking.dto.response.CreateBookingResponse;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceResponse;

public interface BookingService {
    CreateBookingResponse createBooking(CreateBookingRequest request);

    BookingSummaryResponse getBookingSummary(UUID bookingId);

    BookingSummaryResponse redeemPoints(UUID bookingId, DiscountPointRequest pointsToRedeem);
    
    /**
     * Create invoice for confirmed booking
     */
    InvoiceResponse createInvoiceForBooking(UUID bookingId);
    
    /**
     * Confirm booking after successful payment
     */
    void confirmBookingPayment(String bookingId);
}
