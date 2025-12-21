package com.theatermgnt.theatermgnt.bookingCombo.controller;

import com.theatermgnt.theatermgnt.booking.dto.response.BookingPricingResponse;
import com.theatermgnt.theatermgnt.bookingCombo.service.BookingComboService;
import com.theatermgnt.theatermgnt.bookingCombo.dto.request.UpdateBookingCombosRequest;
import com.theatermgnt.theatermgnt.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingComboController {
    private final BookingComboService bookingComboService;

    @PutMapping("/{bookingId}/combos")
    public ApiResponse<BookingPricingResponse> updateCombos(
            @PathVariable String bookingId,
            @RequestBody @Valid UpdateBookingCombosRequest request
    ) {
        return ApiResponse.<BookingPricingResponse>builder()
                .result(bookingComboService.updateCombos(bookingId, request))
                .build();
    }
}
