package com.theatermgnt.theatermgnt.payment.service;

import com.theatermgnt.theatermgnt.payment.dto.response.PaymentDetailsResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentService {
    
    /**
     * Create VNPay payment URL for invoice
     */
    PaymentDetailsResponse createVNPayPayment(String invoiceId, HttpServletRequest httpRequest);
    
    /**
     * Handle VNPay return URL callback
     */
    Map<String, Object> handleVNPayCallback(Map<String, String> params);
    
    /**
     * Handle VNPay IPN (Instant Payment Notification)
     */
    Map<String, Object> handleVNPayIPN(Map<String, String> params);
}
