package com.theatermgnt.theatermgnt.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theatermgnt.theatermgnt.config.VNPayConfig;
import com.theatermgnt.theatermgnt.payment.dto.request.PaymentRequest;
import com.theatermgnt.theatermgnt.payment.dto.request.VNPayCallbackRequest;
import com.theatermgnt.theatermgnt.payment.dto.response.PaymentResponse;
import com.theatermgnt.theatermgnt.payment.entity.Payment;
import com.theatermgnt.theatermgnt.payment.enums.PaymentStatus;
import com.theatermgnt.theatermgnt.payment.repository.PaymentRepository;
import com.theatermgnt.theatermgnt.payment.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final VNPayConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PaymentResponse createPayment(PaymentRequest request, HttpServletRequest httpRequest) {
        try {
            // Generate transaction reference
            String txnRef = request.getOrderId() + "_" + System.currentTimeMillis();
            
            // Create payment record
            Payment payment = Payment.builder()
                    .txnRef(txnRef)
                    .amount(request.getAmount())
                    .orderInfo(request.getOrderInfo() != null ? request.getOrderInfo() : "Payment for order " + request.getOrderId())
                    .customerId(request.getCustomerId())
                    .status(PaymentStatus.PENDING)
                    .build();
            paymentRepository.save(payment);

            // Build VNPay parameters
            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", vnPayConfig.getVersion());
            vnpParams.put("vnp_Command", vnPayConfig.getCommand());
            vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnpParams.put("vnp_Amount", String.valueOf(request.getAmount() * 100)); // VNPay requires amount * 100
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", txnRef);
            vnpParams.put("vnp_OrderInfo", payment.getOrderInfo());
            vnpParams.put("vnp_OrderType", vnPayConfig.getOrderType());
            vnpParams.put("vnp_Locale", request.getLocale() != null ? request.getLocale() : "vn");
            vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
            vnpParams.put("vnp_IpAddr", VNPayUtil.getIpAddress(httpRequest));

            // Add timestamp (VNPay requires GMT+7)
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnpCreateDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_CreateDate", vnpCreateDate);

            // Build hash data
            String hashData = VNPayUtil.hashAllFields(vnpParams);
            String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
            
            // Build payment URL
            String queryUrl = VNPayUtil.getPaymentURL(vnpParams, false);
            String paymentUrl = vnPayConfig.getUrl() + "?" + queryUrl + "&vnp_SecureHash=" + vnpSecureHash;

            log.info("===== VNPAY DEBUG =====");
            log.info("TmnCode: {}", vnPayConfig.getTmnCode());
            log.info("HashSecret: {}", vnPayConfig.getHashSecret().substring(0, 10) + "...");
            log.info("Hash Data: {}", hashData);
            log.info("Secure Hash: {}", vnpSecureHash);
            log.info("Query URL: {}", queryUrl);
            log.info("Full Payment URL: {}", paymentUrl);
            log.info("=======================");
            
            log.info("Created payment with txnRef: {}, paymentUrl: {}", txnRef, paymentUrl);

            return PaymentResponse.builder()
                    .code("00")
                    .message("Success")
                    .paymentUrl(paymentUrl)
                    .build();

        } catch (Exception e) {
            log.error("Error creating payment", e);
            return PaymentResponse.builder()
                    .code("99")
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Map<String, Object> handleVNPayCallback(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verify signature
            String vnpSecureHash = params.get("vnp_SecureHash");
            String vnpSecureHashType = params.get("vnp_SecureHashType");
            
            // Remove hash fields before verification
            params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");
            
            // Use callback hash (raw values, no encoding)
            String hashData = VNPayUtil.hashAllFieldsForCallback(params);
            String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
            
            log.info("VNPay Callback Verification - Hash Data: {}, Calculated: {}, Received: {}", 
                hashData, calculatedHash, vnpSecureHash);
            
            // Compare hashes (case-insensitive)
            if (!calculatedHash.equalsIgnoreCase(vnpSecureHash)) {
                response.put("code", "97");
                response.put("message", "Invalid signature");
                log.warn("VNPay callback signature mismatch");
                return response;
            }

            // Get payment
            String txnRef = params.get("vnp_TxnRef");
            Optional<Payment> paymentOpt = paymentRepository.findByTxnRef(txnRef);
            
            if (paymentOpt.isEmpty()) {
                response.put("code", "01");
                response.put("message", "Payment not found");
                return response;
            }

            Payment payment = paymentOpt.get();
            
            // Update payment
            payment.setBankCode(params.get("vnp_BankCode"));
            payment.setBankTranNo(params.get("vnp_BankTranNo"));
            payment.setCardType(params.get("vnp_CardType"));
            payment.setPaymentDate(params.get("vnp_PayDate"));
            payment.setVnpayTransactionNo(params.get("vnp_TransactionNo"));
            payment.setResponseCode(params.get("vnp_ResponseCode"));
            payment.setTransactionStatus(params.get("vnp_TransactionStatus"));
            
            try {
                payment.setRawCallbackData(objectMapper.writeValueAsString(params));
            } catch (Exception e) {
                log.error("Error serializing callback data", e);
            }

            // Check transaction status
            String responseCode = params.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                payment.setStatus(PaymentStatus.SUCCESS);
                response.put("code", "00");
                response.put("message", "Payment successful");
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                response.put("code", responseCode);
                response.put("message", "Payment failed");
            }
            
            paymentRepository.save(payment);
            
            response.put("txnRef", txnRef);
            response.put("amount", Long.parseLong(params.get("vnp_Amount")) / 100);
            response.put("orderInfo", params.get("vnp_OrderInfo"));
            
        } catch (Exception e) {
            log.error("Error handling VNPay callback", e);
            response.put("code", "99");
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @Override
    public Map<String, Object> handleVNPayIPN(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verify signature
            String vnpSecureHash = params.get("vnp_SecureHash");
            String vnpSecureHashType = params.get("vnp_SecureHashType");
            
            // Remove hash fields before verification
            params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");
            
            // Use callback hash (raw values, no encoding)
            String hashData = VNPayUtil.hashAllFieldsForCallback(params);
            String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
            
            log.info("VNPay IPN Verification - Hash Data: {}, Calculated: {}, Received: {}", 
                hashData, calculatedHash, vnpSecureHash);
            
            // Compare hashes (case-insensitive)
            if (!calculatedHash.equalsIgnoreCase(vnpSecureHash)) {
                response.put("RspCode", "97");
                response.put("Message", "Invalid signature");
                log.warn("VNPay IPN signature mismatch");
                return response;
            }

            String txnRef = params.get("vnp_TxnRef");
            Optional<Payment> paymentOpt = paymentRepository.findByTxnRef(txnRef);
            
            if (paymentOpt.isEmpty()) {
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return response;
            }

            Payment payment = paymentOpt.get();
            
            // Check if already processed
            if (payment.getStatus() != PaymentStatus.PENDING) {
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            // Verify amount
            long vnpAmount = Long.parseLong(params.get("vnp_Amount")) / 100;
            if (!payment.getAmount().equals(vnpAmount)) {
                response.put("RspCode", "04");
                response.put("Message", "Invalid amount");
                return response;
            }

            // Update payment status
            String responseCode = params.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                payment.setStatus(PaymentStatus.SUCCESS);
                
                // TODO: Update your order/booking status here
                // Example: bookingService.confirmBooking(txnRef);
                
                response.put("RspCode", "00");
                response.put("Message", "Confirm success");
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                response.put("RspCode", "00");
                response.put("Message", "Confirm success");
            }

            payment.setResponseCode(responseCode);
            payment.setVnpayTransactionNo(params.get("vnp_TransactionNo"));
            paymentRepository.save(payment);
            
        } catch (Exception e) {
            log.error("Error handling VNPay IPN", e);
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }
        
        return response;
    }
}
