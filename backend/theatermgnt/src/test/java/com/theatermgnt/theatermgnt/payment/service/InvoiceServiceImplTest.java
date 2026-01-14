package com.theatermgnt.theatermgnt.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.theatermgnt.theatermgnt.booking.dto.response.BookingSummaryResponse;
import com.theatermgnt.theatermgnt.booking.entity.Booking;
import com.theatermgnt.theatermgnt.booking.repository.BookingRepository;
import com.theatermgnt.theatermgnt.booking.service.BookingService;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.payment.dto.request.CreateInvoiceRequest;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceDetailResponse;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceResponse;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceStatisticsResponse;
import com.theatermgnt.theatermgnt.payment.entity.Invoice;
import com.theatermgnt.theatermgnt.payment.entity.InvoiceStatus;
import com.theatermgnt.theatermgnt.payment.mapper.InvoiceMapper;
import com.theatermgnt.theatermgnt.payment.repository.InvoiceRepository;
import com.theatermgnt.theatermgnt.revenue.service.RevenueAggregationService;
import com.theatermgnt.theatermgnt.ticket.service.TicketService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InvoiceServiceImplTest {

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BookingService bookingService;

    @Mock
    InvoiceMapper invoiceMapper;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    RevenueAggregationService revenueAggregationService;

    @Mock
    TicketService ticketService;

    @InjectMocks
    InvoiceServiceImpl invoiceService;

    private UUID testBookingId;
    private String testInvoiceId;

    @BeforeEach
    void setUp() {
        testBookingId = UUID.randomUUID();
        testInvoiceId = UUID.randomUUID().toString();
    }

    // ================= CREATE =================

    @Test
    void createInvoice_success() {
        CreateInvoiceRequest req = new CreateInvoiceRequest();
        req.setBookingId(testBookingId.toString());

        Booking booking = new Booking();
        booking.setId(testBookingId);
        booking.setTotalAmount(BigDecimal.valueOf(100000));

        when(bookingRepository.findById(testBookingId)).thenReturn(Optional.of(booking));
        when(invoiceRepository.findByBookingId(testBookingId.toString())).thenReturn(Optional.empty());

        Invoice invoice = new Invoice();
        invoice.setId(testInvoiceId);
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setTotalAmount(BigDecimal.valueOf(100000));

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceMapper.toResponse(invoice)).thenReturn(mock(InvoiceResponse.class));

        InvoiceResponse result = invoiceService.createInvoice(req);

        assertNotNull(result);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void createInvoice_bookingNotFound_throws() {
        CreateInvoiceRequest req = new CreateInvoiceRequest();
        req.setBookingId(testBookingId.toString());

        when(bookingRepository.findById(testBookingId)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> invoiceService.createInvoice(req));
    }

    @Test
    void createInvoice_invoiceAlreadyExists_throws() {
        CreateInvoiceRequest req = new CreateInvoiceRequest();
        req.setBookingId(testBookingId.toString());

        Booking booking = new Booking();
        booking.setId(testBookingId);
        booking.setTotalAmount(BigDecimal.valueOf(100000));

        when(bookingRepository.findById(testBookingId)).thenReturn(Optional.of(booking));
        when(invoiceRepository.findByBookingId(testBookingId.toString()))
                .thenReturn(Optional.of(new Invoice()));

        assertThrows(AppException.class, () -> invoiceService.createInvoice(req));
    }

    // ================= READ =================

    @Test
    void getInvoice_exists_returns() {
        Invoice invoice = new Invoice();
        invoice.setId(testInvoiceId);
        invoice.setStatus(InvoiceStatus.PENDING);

        when(invoiceRepository.findById(testInvoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toResponse(invoice)).thenReturn(mock(InvoiceResponse.class));

        InvoiceResponse result = invoiceService.getInvoice(testInvoiceId);

        assertNotNull(result);
        verify(invoiceMapper).toResponse(invoice);
    }

    @Test
    void getInvoice_notFound_throws() {
        when(invoiceRepository.findById(testInvoiceId)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> invoiceService.getInvoice(testInvoiceId));
    }

    @Test
    void getInvoiceByBookingId_success() {
        Invoice invoice = new Invoice();
        invoice.setId(testInvoiceId);
        invoice.setBookingId(testBookingId.toString());

        when(invoiceRepository.findByBookingId(testBookingId.toString())).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toResponse(invoice)).thenReturn(mock(InvoiceResponse.class));

        InvoiceResponse result = invoiceService.getInvoiceByBookingId(testBookingId.toString());

        assertNotNull(result);
    }

    @Test
    void getInvoiceByBookingId_notFound_throws() {
        when(invoiceRepository.findByBookingId(testBookingId.toString())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> invoiceService.getInvoiceByBookingId(testBookingId.toString()));
    }

    @Test
    void getInvoiceDetail_success() {
        Invoice invoice = new Invoice();
        invoice.setId(testInvoiceId);
        invoice.setBookingId(testBookingId.toString());
        invoice.setTotalAmount(BigDecimal.valueOf(100000));
        invoice.setStatus(InvoiceStatus.PENDING);

        when(invoiceRepository.findById(testInvoiceId)).thenReturn(Optional.of(invoice));
        when(bookingService.getBookingSummary(testBookingId))
                .thenReturn(mock(BookingSummaryResponse.class));

        InvoiceDetailResponse result = invoiceService.getInvoiceDetail(testInvoiceId);

        assertNotNull(result);
        assertEquals(testInvoiceId, result.getId());
        assertEquals(testBookingId.toString(), result.getBookingId());
    }

    @Test
    void getInvoiceDetail_invoiceNotFound_throws() {
        when(invoiceRepository.findById(testInvoiceId)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> invoiceService.getInvoiceDetail(testInvoiceId));
    }

    @Test
    void getAllInvoices_success() {
        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();

        Page<Invoice> invoicePage = new PageImpl<>(
                java.util.List.of(invoice1, invoice2), PageRequest.of(0, 10), 2);

        when(invoiceRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(invoicePage);
        when(invoiceMapper.toResponse(any())).thenReturn(mock(InvoiceResponse.class));

        Page<InvoiceResponse> result = invoiceService.getAllInvoices(0, 10, null);

        assertEquals(2, result.getTotalElements());
        verify(invoiceRepository).findAllByOrderByCreatedAtDesc(any(Pageable.class));
    }

    @Test
    void getAllInvoices_byCinema_success() {
        Invoice invoice = new Invoice();

        Page<Invoice> invoicePage = new PageImpl<>(java.util.List.of(invoice), PageRequest.of(0, 10), 1);

        when(invoiceRepository.findByCinema("cinema1", PageRequest.of(0, 10))).thenReturn(invoicePage);
        when(invoiceMapper.toResponse(any())).thenReturn(mock(InvoiceResponse.class));

        Page<InvoiceResponse> result = invoiceService.getAllInvoices(0, 10, "cinema1");

        assertEquals(1, result.getTotalElements());
        verify(invoiceRepository).findByCinema("cinema1", PageRequest.of(0, 10));
    }

    @Test
    void getInvoicesByStatus_success() {
        Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.PAID);

        Page<Invoice> invoicePage = new PageImpl<>(java.util.List.of(invoice), PageRequest.of(0, 10), 1);

        when(invoiceRepository.findByStatusOrderByCreatedAtDesc(InvoiceStatus.PAID, PageRequest.of(0, 10)))
                .thenReturn(invoicePage);
        when(invoiceMapper.toResponse(any())).thenReturn(mock(InvoiceResponse.class));

        Page<InvoiceResponse> result = invoiceService.getInvoicesByStatus(InvoiceStatus.PAID, 0, 10, null);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getInvoicesByDateRange_success() {
        Invoice invoice = new Invoice();
        LocalDateTime now = LocalDateTime.now();

        Page<Invoice> invoicePage = new PageImpl<>(java.util.List.of(invoice), PageRequest.of(0, 10), 1);

        when(invoiceRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(
                        any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(invoicePage);
        when(invoiceMapper.toResponse(any())).thenReturn(mock(InvoiceResponse.class));

        Page<InvoiceResponse> result = invoiceService.getInvoicesByDateRange(now, now.plusDays(1), 0, 10, null);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchInvoices_success() {
        Invoice invoice = new Invoice();

        Page<Invoice> invoicePage = new PageImpl<>(java.util.List.of(invoice), PageRequest.of(0, 10), 1);

        when(invoiceRepository.searchInvoices("test", PageRequest.of(0, 10))).thenReturn(invoicePage);
        when(invoiceMapper.toResponse(any())).thenReturn(mock(InvoiceResponse.class));

        Page<InvoiceResponse> result = invoiceService.searchInvoices("test", 0, 10, null);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchInvoicesByStatus_success() {
        Invoice invoice = new Invoice();

        Page<Invoice> invoicePage = new PageImpl<>(java.util.List.of(invoice), PageRequest.of(0, 10), 1);

        when(invoiceRepository.searchInvoicesByStatus("test", InvoiceStatus.PAID, PageRequest.of(0, 10)))
                .thenReturn(invoicePage);
        when(invoiceMapper.toResponse(any())).thenReturn(mock(InvoiceResponse.class));

        Page<InvoiceResponse> result = invoiceService.searchInvoicesByStatus("test", InvoiceStatus.PAID, 0, 10, null);

        assertEquals(1, result.getTotalElements());
    }

    // ================= UPDATE =================

    @Test
    void markAsPaid_success() {
        Invoice invoice = new Invoice();
        invoice.setId(testInvoiceId);
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setBookingId(testBookingId.toString());

        when(invoiceRepository.findById(testInvoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(invoiceMapper.toResponse(invoice)).thenReturn(mock(InvoiceResponse.class));

        InvoiceResponse result = invoiceService.markAsPaid(testInvoiceId);

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertNotNull(invoice.getPaidAt());
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void markAsFailed_success() {
        Invoice invoice = new Invoice();
        invoice.setId(testInvoiceId);
        invoice.setStatus(InvoiceStatus.PENDING);

        when(invoiceRepository.findById(testInvoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(invoiceMapper.toResponse(invoice)).thenReturn(mock(InvoiceResponse.class));

        InvoiceResponse result = invoiceService.markAsFailed(testInvoiceId);

        assertEquals(InvoiceStatus.FAILED, invoice.getStatus());
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void updateInvoiceStatus_notFound_throws() {
        when(invoiceRepository.findById(testInvoiceId)).thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> invoiceService.updateInvoiceStatus(testInvoiceId, InvoiceStatus.PAID));
    }

    @Test
    void updateInvoiceStatus_toRefunded_success() {
        Invoice invoice = new Invoice();
        invoice.setId(testInvoiceId);
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setBookingId(testBookingId.toString());

        when(invoiceRepository.findById(testInvoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(invoiceMapper.toResponse(invoice)).thenReturn(mock(InvoiceResponse.class));

        invoiceService.updateInvoiceStatus(testInvoiceId, InvoiceStatus.REFUNDED);

        assertEquals(InvoiceStatus.REFUNDED, invoice.getStatus());
        verify(bookingService).refundBooking(testBookingId.toString());
        verify(ticketService).expireTicketsByBookingId(testBookingId);
        verify(revenueAggregationService).processInvoiceRefundForRevenue(testInvoiceId);
        
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertNotNull(captor.getValue());
    }

    // ================= STATISTICS =================

    @Test
    void getStatistics_global_success() {
        when(invoiceRepository.count()).thenReturn(100L);
        when(invoiceRepository.countByStatus(InvoiceStatus.PENDING)).thenReturn(20L);
        when(invoiceRepository.countByStatus(InvoiceStatus.PAID)).thenReturn(70L);
        when(invoiceRepository.countByStatus(InvoiceStatus.FAILED)).thenReturn(5L);
        when(invoiceRepository.countByStatus(InvoiceStatus.REFUNDED)).thenReturn(5L);
        when(invoiceRepository.sumTotalAmountByStatus(InvoiceStatus.PAID))
                .thenReturn(7000000.0);
        when(invoiceRepository.sumTotalAmountByStatus(InvoiceStatus.PENDING))
                .thenReturn(2000000.0);
        when(invoiceRepository.sumTotalAmountByStatus(InvoiceStatus.REFUNDED))
                .thenReturn(500000.0);

        InvoiceStatisticsResponse result = invoiceService.getStatistics(null);

        assertNotNull(result);
        assertEquals(100L, result.getTotalInvoices());
        assertEquals(20L, result.getPendingInvoices());
        assertEquals(70L, result.getPaidInvoices());
        assertEquals(5L, result.getFailedInvoices());
        assertEquals(5L, result.getRefundedInvoices());
        assertEquals(BigDecimal.valueOf(7000000.0), result.getTotalRevenue());
    }

    @Test
    void getStatistics_byCinema_success() {
        when(invoiceRepository.countByCinema("cinema1")).thenReturn(50L);
        when(invoiceRepository.countByCinemaAndStatus("cinema1", "PENDING")).thenReturn(10L);
        when(invoiceRepository.countByCinemaAndStatus("cinema1", "PAID")).thenReturn(35L);
        when(invoiceRepository.countByCinemaAndStatus("cinema1", "FAILED")).thenReturn(3L);
        when(invoiceRepository.countByCinemaAndStatus("cinema1", "REFUNDED")).thenReturn(2L);
        when(invoiceRepository.sumTotalAmountByCinemaAndStatus("cinema1", "PAID"))
                .thenReturn(3500000.0);
        when(invoiceRepository.sumTotalAmountByCinemaAndStatus("cinema1", "PENDING"))
                .thenReturn(1000000.0);
        when(invoiceRepository.sumTotalAmountByCinemaAndStatus("cinema1", "REFUNDED"))
                .thenReturn(250000.0);

        InvoiceStatisticsResponse result = invoiceService.getStatistics("cinema1");

        assertNotNull(result);
        assertEquals(50L, result.getTotalInvoices());
        assertEquals(10L, result.getPendingInvoices());
        assertEquals(35L, result.getPaidInvoices());
    }
}
