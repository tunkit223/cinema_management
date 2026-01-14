package com.theatermgnt.theatermgnt.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theatermgnt.theatermgnt.booking.service.BookingService;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.config.VNPayConfig;
import com.theatermgnt.theatermgnt.payment.dto.response.PaymentDetailsResponse;
import com.theatermgnt.theatermgnt.payment.entity.Invoice;
import com.theatermgnt.theatermgnt.payment.entity.InvoiceStatus;
import com.theatermgnt.theatermgnt.payment.entity.Payment;
import com.theatermgnt.theatermgnt.payment.entity.PaymentMethod;
import com.theatermgnt.theatermgnt.payment.entity.PaymentType;
import com.theatermgnt.theatermgnt.payment.enums.PaymentStatus;
import com.theatermgnt.theatermgnt.payment.mapper.PaymentMapper;
import com.theatermgnt.theatermgnt.payment.repository.InvoiceRepository;
import com.theatermgnt.theatermgnt.payment.repository.PaymentMethodRepository;
import com.theatermgnt.theatermgnt.payment.repository.PaymentRepository;
import com.theatermgnt.theatermgnt.revenue.service.RevenueAggregationService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceImplTest {

    @Mock
    VNPayConfig vnPayConfig;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    PaymentMethodRepository paymentMethodRepository;

    @Mock
    InvoiceService invoiceService;

    @Mock
    PaymentMapper paymentMapper;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    BookingService bookingService;

    @Mock
    RevenueAggregationService revenueAggregationService;

    @InjectMocks
    PaymentServiceImpl paymentService;

    private HttpServletRequest mockHttpRequest;

    @BeforeEach
    void setUp() {
        mockHttpRequest = mock(HttpServletRequest.class);
        when(mockHttpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    // ================= CREATE VNPAY PAYMENT =================

    @Test
    void createVNPayPayment_success() {
        Invoice invoice = new Invoice();
        invoice.setId("inv1");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setTotalAmount(BigDecimal.valueOf(100000));

        when(invoiceRepository.findById("inv1")).thenReturn(Optional.of(invoice));

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("pm1");
        paymentMethod.setName("VNPay");
        when(paymentMethodRepository.findByName("VNPay")).thenReturn(Optional.of(paymentMethod));

        Payment payment = new Payment();
        payment.setId("p1");
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        when(vnPayConfig.getVersion()).thenReturn("2.1.0");
        when(vnPayConfig.getCommand()).thenReturn("pay");
        when(vnPayConfig.getTmnCode()).thenReturn("TESTMERCHANT");
        when(vnPayConfig.getOrderType()).thenReturn("order");
        when(vnPayConfig.getReturnUrl()).thenReturn("http://localhost:3000/payment/callback");
        when(vnPayConfig.getUrl()).thenReturn("https://sandbox.vnpayment.vn/paygate");
        when(vnPayConfig.getHashSecret()).thenReturn("TESTSECRET");

        PaymentDetailsResponse result = paymentService.createVNPayPayment("inv1", mockHttpRequest, null);

        assertNotNull(result);
        assertEquals("00", result.getCode());
        assertEquals("Success", result.getMessage());
        assertNotNull(result.getPaymentUrl());
        assertEquals("inv1", result.getInvoiceId());
        assertEquals(BigDecimal.valueOf(100000), result.getAmount());
        assertEquals(PaymentStatus.PENDING.name(), result.getStatus());

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void createVNPayPayment_invoiceNotFound_throws() {
        when(invoiceRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> paymentService.createVNPayPayment("nonexistent", mockHttpRequest, null));
    }

    @Test
    void createVNPayPayment_invoiceAlreadyPaid_throws() {
        Invoice invoice = new Invoice();
        invoice.setId("inv1");
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setTotalAmount(BigDecimal.valueOf(100000));

        when(invoiceRepository.findById("inv1")).thenReturn(Optional.of(invoice));

        assertThrows(AppException.class, () -> paymentService.createVNPayPayment("inv1", mockHttpRequest, null));
    }

    @Test
    void createVNPayPayment_paymentMethodNotFound_throws() {
        Invoice invoice = new Invoice();
        invoice.setId("inv1");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setTotalAmount(BigDecimal.valueOf(100000));

        when(invoiceRepository.findById("inv1")).thenReturn(Optional.of(invoice));
        when(paymentMethodRepository.findByName("VNPay")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> paymentService.createVNPayPayment("inv1", mockHttpRequest, null));
    }

    @Test
    void createVNPayPayment_withReturnUrlOverride() {
        Invoice invoice = new Invoice();
        invoice.setId("inv1");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setTotalAmount(BigDecimal.valueOf(100000));

        when(invoiceRepository.findById("inv1")).thenReturn(Optional.of(invoice));

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("pm1");
        paymentMethod.setName("VNPay");
        when(paymentMethodRepository.findByName("VNPay")).thenReturn(Optional.of(paymentMethod));

        Payment payment = new Payment();
        payment.setId("p1");
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        when(vnPayConfig.getVersion()).thenReturn("2.1.0");
        when(vnPayConfig.getCommand()).thenReturn("pay");
        when(vnPayConfig.getTmnCode()).thenReturn("TESTMERCHANT");
        when(vnPayConfig.getOrderType()).thenReturn("order");
        when(vnPayConfig.getReturnUrl()).thenReturn("http://localhost:3000/payment/callback");
        when(vnPayConfig.getUrl()).thenReturn("https://sandbox.vnpayment.vn/paygate");
        when(vnPayConfig.getHashSecret()).thenReturn("TESTSECRET");

        String customReturnUrl = "http://custom-url:8080/callback";
        PaymentDetailsResponse result =
                paymentService.createVNPayPayment("inv1", mockHttpRequest, customReturnUrl);

        assertNotNull(result);
        assertTrue(result.getPaymentUrl().contains("custom-url"));
    }

    // ================= PROCESS CASH PAYMENT =================

    @Test
    void processCashPayment_success() {
        Invoice invoice = new Invoice();
        invoice.setId("inv1");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setTotalAmount(BigDecimal.valueOf(100000));
        invoice.setBookingId("book1");

        when(invoiceRepository.findById("inv1")).thenReturn(Optional.of(invoice));

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("pm2");
        paymentMethod.setName("Cash");
        when(paymentMethodRepository.findByName("Cash")).thenReturn(Optional.of(paymentMethod));

        Payment payment = new Payment();
        payment.setId("p2");
        payment.setStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentDetailsResponse result = paymentService.processCashPayment("inv1");

        assertNotNull(result);
        assertEquals("00", result.getCode());
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        verify(paymentRepository).save(any(Payment.class));
        verify(bookingService).confirmBookingPayment("book1");
        verify(revenueAggregationService).processPaymentForRevenue(any());
    }

    @Test
    void processCashPayment_invoiceNotFound_throws() {
        when(invoiceRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> paymentService.processCashPayment("nonexistent"));
    }

    // ================= HANDLE VNPAY CALLBACK =================

    @Test
    void handleVNPayCallback_success() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TransactionStatus", "00");
        params.put("vnp_TxnRef", "txn123");

        when(invoiceService.markAsPaid(anyString()))
                .thenReturn(mock(com.theatermgnt.theatermgnt.payment.dto.response.InvoiceResponse.class));

        Map<String, Object> result = paymentService.handleVNPayCallback(params);

        assertNotNull(result);
    }

    @Test
    void handleVNPayCallback_failedTransaction() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TransactionStatus", "01");
        params.put("vnp_TxnRef", "txn123");

        Map<String, Object> result = paymentService.handleVNPayCallback(params);

        assertNotNull(result);
    }

    // ================= HANDLE VNPAY IPN =================

    @Test
    void handleVNPayIPN_success() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TransactionStatus", "00");
        params.put("vnp_TxnRef", "txn123");

        when(invoiceService.markAsPaid(anyString()))
                .thenReturn(mock(com.theatermgnt.theatermgnt.payment.dto.response.InvoiceResponse.class));

        Map<String, Object> result = paymentService.handleVNPayIPN(params);

        assertNotNull(result);
    }
}
