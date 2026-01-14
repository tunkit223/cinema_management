package com.theatermgnt.theatermgnt.booking.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.theatermgnt.theatermgnt.booking.dto.request.CreateBookingRequest;
import com.theatermgnt.theatermgnt.booking.dto.request.DiscountPointRequest;
import com.theatermgnt.theatermgnt.booking.dto.response.BookingListResponse;
import com.theatermgnt.theatermgnt.booking.dto.response.BookingSummaryResponse;
import com.theatermgnt.theatermgnt.booking.dto.response.CreateBookingResponse;
import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;

public interface BookingService {
    CreateBookingResponse createBooking(CreateBookingRequest request);

    BookingSummaryResponse getBookingSummary(UUID bookingId);

    BookingSummaryResponse redeemPoints(UUID bookingId, DiscountPointRequest pointsToRedeem);

    void cancelBooking(UUID bookingId);

    /**
     * Confirm booking after successful payment
     */
    void confirmBookingPayment(String bookingId);

    /**
     * Refund booking and update status to REFUNDED
     */
    void refundBooking(String bookingId);

    /**
     * Get list of bookings with filters and pagination
     */
    BookingListResponse getBookings(
            BookingStatus status, String customerSearch, String emailSearch, String movieSearch, Pageable pageable);
}
