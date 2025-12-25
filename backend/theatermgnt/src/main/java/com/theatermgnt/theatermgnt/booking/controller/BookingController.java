package com.theatermgnt.theatermgnt.booking.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.theatermgnt.theatermgnt.booking.dto.request.CreateBookingRequest;
import com.theatermgnt.theatermgnt.booking.dto.response.BookingSummaryResponse;
import com.theatermgnt.theatermgnt.booking.dto.response.CreateBookingResponse;
import com.theatermgnt.theatermgnt.booking.service.BookingService;
import com.theatermgnt.theatermgnt.common.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ApiResponse<CreateBookingResponse> createBooking(@RequestBody @Valid CreateBookingRequest request) {
        return ApiResponse.<CreateBookingResponse>builder()
                .result(bookingService.createBooking(request))
                .build();
    }

    @GetMapping("/{bookingId}/summary")
    public ApiResponse<BookingSummaryResponse> getSummary(@PathVariable String bookingId) {
        return ApiResponse.<BookingSummaryResponse>builder()
                .result(bookingService.getBookingSummary(bookingId))
                .build();
    }
}
