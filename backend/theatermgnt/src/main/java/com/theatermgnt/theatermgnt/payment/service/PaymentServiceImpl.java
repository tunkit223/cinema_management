package com.theatermgnt.theatermgnt.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theatermgnt.theatermgnt.config.VNPayConfig;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
import com.theatermgnt.theatermgnt.payment.dto.response.PaymentDetailsResponse;
import com.theatermgnt.theatermgnt.payment.entity.Invoice;
import com.theatermgnt.theatermgnt.payment.entity.InvoiceStatus;
import com.theatermgnt.theatermgnt.payment.entity.Payment;
import com.theatermgnt.theatermgnt.payment.entity.PaymentType;
import com.theatermgnt.theatermgnt.payment.enums.PaymentStatus;
import com.theatermgnt.theatermgnt.payment.mapper.PaymentMapper;
import com.theatermgnt.theatermgnt.payment.repository.InvoiceRepository;
import com.theatermgnt.theatermgnt.payment.repository.PaymentRepository;
import com.theatermgnt.theatermgnt.payment.repository.PaymentMethodRepository;
import com.theatermgnt.theatermgnt.payment.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final VNPayConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final InvoiceService invoiceService;
    private final PaymentMapper paymentMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PaymentDetailsResponse createVNPayPayment(String invoiceId, HttpServletRequest httpRequest) {
        try {
            // Get invoice
            Invoice invoice = invoiceRepository
                    .findById(invoiceId)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
            
            // Check if invoice is already paid
            if (invoice.getStatus() == InvoiceStatus.PAID) {
                throw new AppException(ErrorCode.BOOKING_NOT_EXISTED); // custom error
            }
            
            // Generate transaction reference
            String txnRef = invoiceId + "_" + System.currentTimeMillis();
            
            // Get VNPay payment method
            var vnpayMethod = paymentMethodRepository.findByName("VNPay")
                    .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
            
            // Create payment record
            Payment payment = Payment.builder()
                    .invoiceId(invoiceId)
                    .paymentMethodId(vnpayMethod.getId())
                    .amount(invoice.getTotalAmount())
                    .paymentType(PaymentType.BOOKING)
                    .transactionCode(txnRef)
                    .status(PaymentStatus.PENDING)
                    .description("Payment for invoice: " + invoiceId)
                    .build();
            paymentRepository.save(payment);

            // Build VNPay parameters
            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", vnPayConfig.getVersion());
            vnpParams.put("vnp_Command", vnPayConfig.getCommand());
            vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnpParams.put("vnp_Amount", String.valueOf(invoice.getTotalAmount().longValue() * 100)); // VNPay requires amount * 100
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", txnRef);
            vnpParams.put("vnp_OrderInfo", "Payment for invoice " + invoiceId);
            vnpParams.put("vnp_OrderType", vnPayConfig.getOrderType());
            vnpParams.put("vnp_Locale", "vn");
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
            log.info("InvoiceId: {}", invoiceId);
            log.info("Amount: {}", invoice.getTotalAmount());
            log.info("TxnRef: {}", txnRef);
            log.info("HashSecret: {}", vnPayConfig.getHashSecret().substring(0, 10) + "...");
            log.info("Hash Data: {}", hashData);
            log.info("Secure Hash: {}", vnpSecureHash);
            log.info("Full Payment URL: {}", paymentUrl);
            log.info("=======================");
            
            log.info("Created VNPay payment with txnRef: {}, invoiceId: {}", txnRef, invoiceId);

            return PaymentDetailsResponse.builder()
                    .code("00")
                    .message("Success")
                    .paymentUrl(paymentUrl)
                    .id(payment.getId())
                    .transactionCode(txnRef)
                    .invoiceId(invoiceId)
                    .amount(invoice.getTotalAmount())
                    .status(PaymentStatus.PENDING.name())
                    .build();

        } catch (AppException e) {
            log.error("AppException creating VNPay payment: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating VNPay payment", e);
            throw new AppException(ErrorCode.BOOKING_NOT_EXISTED);
        }
    }

    @Override
    public Map<String, Object> handleVNPayCallback(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verify signature
            String vnpSecureHash = params.get("vnp_SecureHash");
            
            // Remove hash field before verification
            params.remove("vnp_SecureHash");
            
            // Use callback hash (raw values, no encoding)
            String hashData = VNPayUtil.hashAllFieldsForCallback(params);
            String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
            
            log.info("VNPay Callback Verification");
            
            // Compare hashes (case-insensitive)
            if (!calculatedHash.equalsIgnoreCase(vnpSecureHash)) {
                response.put("code", "97");
                response.put("message", "Invalid signature");
                log.warn("VNPay callback signature mismatch");
                return response;
            }

            // Get payment by transaction code
            String txnRef = params.get("vnp_TxnRef");
            Optional<Payment> paymentOpt = paymentRepository.findByTransactionCode(txnRef);
            
            if (paymentOpt.isEmpty()) {
                response.put("code", "01");
                response.put("message", "Payment not found");
                return response;
            }

            Payment payment = paymentOpt.get();
            String responseCode = params.get("vnp_ResponseCode");
            
            // Update payment
            payment.setPaymentDate(LocalDateTime.now());
            if ("00".equals(responseCode)) {
                payment.setStatus(PaymentStatus.SUCCESS);
                
                // Update invoice status to PAID
                Optional<Invoice> invoiceOpt = invoiceRepository.findById(payment.getInvoiceId());
                if (invoiceOpt.isPresent()) {
                    Invoice invoice = invoiceOpt.get();
                    invoice.setStatus(InvoiceStatus.PAID);
                    invoice.setPaidAt(LocalDateTime.now());
                    invoiceRepository.save(invoice);
                    log.info("Invoice {} marked as PAID", invoice.getId());
                }
                
                response.put("code", "00");
                response.put("message", "Payment successful");
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                response.put("code", responseCode);
                response.put("message", "Payment failed with code: " + responseCode);
            }
            
            paymentRepository.save(payment);
            
            response.put("paymentId", payment.getId());
            response.put("invoiceId", payment.getInvoiceId());
            response.put("txnRef", txnRef);
            response.put("amount", Long.parseLong(params.get("vnp_Amount")) / 100);
            
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
            
            // Remove hash field before verification
            params.remove("vnp_SecureHash");
            
            // Use callback hash (raw values, no encoding)
            String hashData = VNPayUtil.hashAllFieldsForCallback(params);
            String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
            
            log.info("VNPay IPN Verification");
            
            // Compare hashes (case-insensitive)
            if (!calculatedHash.equalsIgnoreCase(vnpSecureHash)) {
                response.put("RspCode", "97");
                response.put("Message", "Invalid signature");
                log.warn("VNPay IPN signature mismatch");
                return response;
            }

            String txnRef = params.get("vnp_TxnRef");
            Optional<Payment> paymentOpt = paymentRepository.findByTransactionCode(txnRef);
            
            if (paymentOpt.isEmpty()) {
                response.put("RspCode", "01");
                response.put("Message", "Payment not found");
                return response;
            }

            Payment payment = paymentOpt.get();
            
            // Check if already processed
            if (payment.getStatus() != PaymentStatus.PENDING) {
                response.put("RspCode", "02");
                response.put("Message", "Payment already processed");
                return response;
            }

            // Verify amount
            long vnpAmount = Long.parseLong(params.get("vnp_Amount")) / 100;
            if (payment.getAmount().longValue() != vnpAmount) {
                response.put("RspCode", "04");
                response.put("Message", "Invalid amount");
                return response;
            }

            // Update payment status
            String responseCode = params.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaymentDate(LocalDateTime.now());
                
                // Update invoice status to PAID
                Optional<Invoice> invoiceOpt = invoiceRepository.findById(payment.getInvoiceId());
                if (invoiceOpt.isPresent()) {
                    Invoice invoice = invoiceOpt.get();
                    invoice.setStatus(InvoiceStatus.PAID);
                    invoice.setPaidAt(LocalDateTime.now());
                    invoiceRepository.save(invoice);
                    
                    // TODO: Update Booking status to CONFIRMED
                    // bookingService.confirmBookingPayment(invoice.getBookingId());
                    
                    log.info("Invoice {} marked as PAID and payment success", invoice.getId());
                }
                
                response.put("RspCode", "00");
                response.put("Message", "Confirm success");
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                response.put("RspCode", "00");
                response.put("Message", "Confirm success");
            }

            paymentRepository.save(payment);
            
        } catch (Exception e) {
            log.error("Error handling VNPay IPN", e);
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }
        
        return response;
    }
}
