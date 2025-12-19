package com.theatermgnt.theatermgnt.payment.service;

import com.theatermgnt.theatermgnt.payment.dto.request.PaymentRequest;
import com.theatermgnt.theatermgnt.payment.dto.response.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest);
    
    Map<String, Object> handleVNPayCallback(Map<String, String> params);
    
    Map<String, Object> handleVNPayIPN(Map<String, String> params);
}
