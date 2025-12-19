package com.theatermgnt.theatermgnt.payment.controller;

import com.theatermgnt.theatermgnt.payment.dto.request.PaymentRequest;
import com.theatermgnt.theatermgnt.payment.dto.response.PaymentResponse;
import com.theatermgnt.theatermgnt.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
     * Create VNPay payment URL
     * POST /api/theater-mgnt/payment/create
     */
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request,
            HttpServletRequest httpRequest) {
        log.info("Creating payment for order: {}", request.getOrderId());
        PaymentResponse response = paymentService.createPayment(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * VNPay return URL (user redirect after payment)
     * GET /api/theater-mgnt/payment/vnpay-return
     */
    @GetMapping("/vnpay-return")
    public ResponseEntity<Map<String, Object>> vnpayReturn(@RequestParam Map<String, String> params) {
        log.info("VNPay return callback: {}", params);
        Map<String, Object> response = paymentService.handleVNPayCallback(params);
        return ResponseEntity.ok(response);
    }

    /**
     * VNPay IPN (Instant Payment Notification)
     * GET /api/theater-mgnt/payment/vnpay-ipn
     */
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, Object>> vnpayIPN(@RequestParam Map<String, String> params) {
        log.info("VNPay IPN callback: {}", params);
        Map<String, Object> response = paymentService.handleVNPayIPN(params);
        return ResponseEntity.ok(response);
    }
}
