package com.theatermgnt.theatermgnt.payment.controller;

import com.theatermgnt.theatermgnt.common.dto.response.ApiResponse;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceResponse;
import com.theatermgnt.theatermgnt.payment.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    @GetMapping("/{invoiceId}")
    public ApiResponse<InvoiceResponse> getInvoice(@PathVariable String invoiceId) {
        log.info("Getting invoice: {}", invoiceId);
        return ApiResponse.<InvoiceResponse>builder()
                .result(invoiceService.getInvoice(invoiceId))
                .build();
    }
    
    @GetMapping("/booking/{bookingId}")
    public ApiResponse<InvoiceResponse> getInvoiceByBookingId(@PathVariable String bookingId) {
        log.info("Getting invoice by booking: {}", bookingId);
        return ApiResponse.<InvoiceResponse>builder()
                .result(invoiceService.getInvoiceByBookingId(bookingId))
                .build();
    }
}
