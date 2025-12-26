package com.theatermgnt.theatermgnt.payment.controller;

import com.theatermgnt.theatermgnt.payment.dto.response.PaymentDetailsResponse;
import com.theatermgnt.theatermgnt.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create VNPay payment URL for invoice
     * POST /api/theater-mgnt/payment/vnpay/{invoiceId}
     */
    @PostMapping("/vnpay/{invoiceId}")
    public ResponseEntity<PaymentDetailsResponse> createVNPayPayment(
            @PathVariable String invoiceId,
            HttpServletRequest httpRequest) {
        log.info("Creating VNPay payment for invoice: {}", invoiceId);
        PaymentDetailsResponse response = paymentService.createVNPayPayment(invoiceId, httpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * VNPay return URL (user redirect after payment)
     * GET /api/theater-mgnt/payment/vnpay-return
     */
    @GetMapping("/vnpay-return")
    public ResponseEntity<Map<String, Object>> vnpayReturn(@RequestParam Map<String, String> params) {
        log.info("VNPay return callback");
        Map<String, Object> response = paymentService.handleVNPayCallback(params);
        return ResponseEntity.ok(response);
    }

    /**
     * VNPay IPN (Instant Payment Notification)
     * GET /api/theater-mgnt/payment/vnpay-ipn
     */
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, Object>> vnpayIPN(@RequestParam Map<String, String> params) {
        log.info("VNPay IPN callback");
        Map<String, Object> response = paymentService.handleVNPayIPN(params);
        return ResponseEntity.ok(response);
    }
}
