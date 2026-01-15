// package com.theatermgnt.theatermgnt.booking.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.*;
//
// import java.math.BigDecimal;
// import java.time.Instant;
// import java.util.*;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.mockito.junit.jupiter.MockitoSettings;
// import org.mockito.quality.Strictness;
//
// import com.theatermgnt.theatermgnt.booking.entity.Booking;
// import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;
// import com.theatermgnt.theatermgnt.booking.repository.BookingRepository;
// import com.theatermgnt.theatermgnt.payment.entity.Invoice;
// import com.theatermgnt.theatermgnt.payment.entity.InvoiceStatus;
// import com.theatermgnt.theatermgnt.payment.entity.Payment;
// import com.theatermgnt.theatermgnt.payment.enums.PaymentStatus;
// import com.theatermgnt.theatermgnt.payment.repository.InvoiceRepository;
// import com.theatermgnt.theatermgnt.payment.repository.PaymentRepository;
// import com.theatermgnt.theatermgnt.screeningSeat.repository.ScreeningSeatRepository;
//
// @ExtendWith(MockitoExtension.class)
// @MockitoSettings(strictness = Strictness.LENIENT)
// class BookingExpirationServiceTest {
//
//    @Mock
//    private BookingRepository bookingRepository;
//
//    @Mock
//    private ScreeningSeatRepository screeningSeatRepository;
//
//    @Mock
//    private InvoiceRepository invoiceRepository;
//
//    @Mock
//    private PaymentRepository paymentRepository;
//
//    @InjectMocks
//    private BookingExpirationService expirationService;
//
//    private Booking expiredBooking;
//    private Invoice invoice;
//    private Payment payment;
//
//    @BeforeEach
//    void setUp() {
//        // Setup expired booking
//        expiredBooking = new Booking();
//        expiredBooking.setId(UUID.randomUUID());
//        expiredBooking.setStatus(BookingStatus.PENDING);
//        expiredBooking.setSubtotal(BigDecimal.valueOf(100000));
//        expiredBooking.setTotalAmount(BigDecimal.valueOf(100000));
//        expiredBooking.setDiscount(BigDecimal.ZERO);
//        expiredBooking.setCreatedAt(Instant.now().minusSeconds(700));
//        expiredBooking.setExpiredAt(Instant.now().minusSeconds(100)); // Expired 100 seconds ago
//
//        // Setup invoice
//        invoice = new Invoice();
//        invoice.setId(UUID.randomUUID().toString());
//        invoice.setBookingId(expiredBooking.getId().toString());
//        invoice.setStatus(InvoiceStatus.PENDING);
//        invoice.setTotalAmount(BigDecimal.valueOf(100000));
//
//        // Setup payment
//        payment = new Payment();
//        payment.setId(UUID.randomUUID().toString());
//        payment.setInvoiceId(invoice.getId());
//        payment.setStatus(PaymentStatus.PENDING);
//        payment.setAmount(BigDecimal.valueOf(100000));
//    }
//
//    // ================= EXPIRE BOOKINGS TESTS =================
//
//    @Test
//    void expireBookings_success_withExpiredBookings() {
//        // Given
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.of(invoice));
//        when(paymentRepository.findByInvoiceId(invoice.getId())).thenReturn(List.of(payment));
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        ArgumentCaptor<List<Booking>> bookingCaptor = ArgumentCaptor.forClass(List.class);
//        verify(bookingRepository).saveAll(bookingCaptor.capture());
//
//        List<Booking> savedBookings = bookingCaptor.getValue();
//        assertEquals(1, savedBookings.size());
//        assertEquals(BookingStatus.EXPIRED, savedBookings.get(0).getStatus());
//
//        verify(screeningSeatRepository)
//                .releaseSeatsByBooking(expiredBooking.getId().toString());
//        verify(invoiceRepository).save(invoice);
//        assertEquals(InvoiceStatus.FAILED, invoice.getStatus());
//        verify(paymentRepository).save(payment);
//        assertEquals(PaymentStatus.FAILED, payment.getStatus());
//    }
//
//    @Test
//    void expireBookings_doesNothing_whenNoExpiredBookings() {
//        // Given
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of());
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        verify(bookingRepository, never()).saveAll(anyList());
//        verify(screeningSeatRepository, never()).releaseSeatsByBooking(anyString());
//        verify(invoiceRepository, never()).save(any(Invoice.class));
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }
//
//    @Test
//    void expireBookings_success_withMultipleExpiredBookings() {
//        // Given
//        Booking booking2 = new Booking();
//        booking2.setId(UUID.randomUUID());
//        booking2.setStatus(BookingStatus.PENDING);
//        booking2.setSubtotal(BigDecimal.valueOf(200000));
//        booking2.setTotalAmount(BigDecimal.valueOf(200000));
//        booking2.setCreatedAt(Instant.now().minusSeconds(800));
//        booking2.setExpiredAt(Instant.now().minusSeconds(200));
//
//        Booking booking3 = new Booking();
//        booking3.setId(UUID.randomUUID());
//        booking3.setStatus(BookingStatus.PENDING);
//        booking3.setSubtotal(BigDecimal.valueOf(150000));
//        booking3.setTotalAmount(BigDecimal.valueOf(150000));
//        booking3.setCreatedAt(Instant.now().minusSeconds(900));
//        booking3.setExpiredAt(Instant.now().minusSeconds(300));
//
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class)))
//                .thenReturn(List.of(expiredBooking, booking2, booking3));
//        when(invoiceRepository.findByBookingId(anyString())).thenReturn(Optional.empty());
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        ArgumentCaptor<List<Booking>> captor = ArgumentCaptor.forClass(List.class);
//        verify(bookingRepository).saveAll(captor.capture());
//
//        List<Booking> savedBookings = captor.getValue();
//        assertEquals(3, savedBookings.size());
//        assertTrue(savedBookings.stream().allMatch(b -> b.getStatus() == BookingStatus.EXPIRED));
//        verify(screeningSeatRepository, times(3)).releaseSeatsByBooking(anyString());
//    }
//
//    @Test
//    void expireBookings_handlesBookingWithoutInvoice() {
//        // Given
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.empty());
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        verify(bookingRepository).saveAll(anyList());
//        verify(screeningSeatRepository)
//                .releaseSeatsByBooking(expiredBooking.getId().toString());
//        verify(invoiceRepository, never()).save(any(Invoice.class));
//        verify(paymentRepository, never()).findByInvoiceId(anyString());
//    }
//
//    @Test
//    void expireBookings_doesNotUpdateInvoice_whenAlreadyFailed() {
//        // Given
//        invoice.setStatus(InvoiceStatus.FAILED);
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.of(invoice));
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        verify(bookingRepository).saveAll(anyList());
//        verify(screeningSeatRepository)
//                .releaseSeatsByBooking(expiredBooking.getId().toString());
//        verify(invoiceRepository, never()).save(any(Invoice.class));
//        verify(paymentRepository, never()).findByInvoiceId(anyString());
//    }
//
//    @Test
//    void expireBookings_doesNotUpdateInvoice_whenAlreadyPaid() {
//        // Given
//        invoice.setStatus(InvoiceStatus.PAID);
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.of(invoice));
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        verify(bookingRepository).saveAll(anyList());
//        verify(screeningSeatRepository)
//                .releaseSeatsByBooking(expiredBooking.getId().toString());
//        verify(invoiceRepository, never()).save(any(Invoice.class));
//        verify(paymentRepository, never()).findByInvoiceId(anyString());
//    }
//
//    @Test
//    void expireBookings_handlesMultiplePayments() {
//        // Given
//        Payment payment2 = new Payment();
//        payment2.setId(UUID.randomUUID().toString());
//        payment2.setInvoiceId(invoice.getId());
//        payment2.setStatus(PaymentStatus.PENDING);
//        payment2.setAmount(BigDecimal.valueOf(50000));
//
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.of(invoice));
//        when(paymentRepository.findByInvoiceId(invoice.getId())).thenReturn(List.of(payment, payment2));
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        verify(paymentRepository, times(2)).save(any(Payment.class));
//        assertEquals(PaymentStatus.FAILED, payment.getStatus());
//        assertEquals(PaymentStatus.FAILED, payment2.getStatus());
//    }
//
//    @Test
//    void expireBookings_doesNotUpdatePayment_whenAlreadyFailed() {
//        // Given
//        payment.setStatus(PaymentStatus.FAILED);
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.of(invoice));
//        when(paymentRepository.findByInvoiceId(invoice.getId())).thenReturn(List.of(payment));
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        verify(invoiceRepository).save(invoice);
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }
//
//    @Test
//    void expireBookings_doesNotUpdatePayment_whenAlreadySuccess() {
//        // Given
//        payment.setStatus(PaymentStatus.SUCCESS);
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.of(invoice));
//        when(paymentRepository.findByInvoiceId(invoice.getId())).thenReturn(List.of(payment));
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        verify(invoiceRepository).save(invoice);
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }
//
//    @Test
//    void expireBookings_handlesInvoiceWithNoPayments() {
//        // Given
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.of(invoice));
//        when(paymentRepository.findByInvoiceId(invoice.getId())).thenReturn(List.of());
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        verify(bookingRepository).saveAll(anyList());
//        verify(invoiceRepository).save(invoice);
//        assertEquals(InvoiceStatus.FAILED, invoice.getStatus());
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }
//
//    @Test
//    void expireBookings_releasesSeatsForAllExpiredBookings() {
//        // Given
//        Booking booking2 = new Booking();
//        booking2.setId(UUID.randomUUID());
//        booking2.setStatus(BookingStatus.PENDING);
//        booking2.setExpiredAt(Instant.now().minusSeconds(50));
//
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class)))
//                .thenReturn(List.of(expiredBooking, booking2));
//        when(invoiceRepository.findByBookingId(anyString())).thenReturn(Optional.empty());
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        verify(screeningSeatRepository)
//                .releaseSeatsByBooking(expiredBooking.getId().toString());
//        verify(screeningSeatRepository).releaseSeatsByBooking(booking2.getId().toString());
//        verify(screeningSeatRepository, times(2)).releaseSeatsByBooking(anyString());
//    }
//
//    @Test
//    void expireBookings_updatesAllBookingStatuses() {
//        // Given
//        Booking booking2 = new Booking();
//        booking2.setId(UUID.randomUUID());
//        booking2.setStatus(BookingStatus.PENDING);
//        booking2.setExpiredAt(Instant.now().minusSeconds(150));
//
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class)))
//                .thenReturn(List.of(expiredBooking, booking2));
//        when(invoiceRepository.findByBookingId(anyString())).thenReturn(Optional.empty());
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        ArgumentCaptor<List<Booking>> captor = ArgumentCaptor.forClass(List.class);
//        verify(bookingRepository).saveAll(captor.capture());
//
//        List<Booking> savedBookings = captor.getValue();
//        assertEquals(2, savedBookings.size());
//        assertEquals(BookingStatus.EXPIRED, savedBookings.get(0).getStatus());
//        assertEquals(BookingStatus.EXPIRED, savedBookings.get(1).getStatus());
//    }
//
//    @Test
//    void expireBookings_handlesMixedPaymentStatuses() {
//        // Given
//        Payment payment2 = new Payment();
//        payment2.setId(UUID.randomUUID().toString());
//        payment2.setInvoiceId(invoice.getId());
//        payment2.setStatus(PaymentStatus.SUCCESS); // Already successful
//
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.of(invoice));
//        when(paymentRepository.findByInvoiceId(invoice.getId())).thenReturn(List.of(payment, payment2));
//
//        // When
//        expirationService.expireBookings();
//
//        // Then
//        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
//        verify(paymentRepository).save(paymentCaptor.capture());
//
//        // Only pending payment should be updated
//        assertEquals(PaymentStatus.FAILED, payment.getStatus());
//        assertEquals(PaymentStatus.SUCCESS, payment2.getStatus());
//    }
//
//    @Test
//    void expireBookings_verifiesCorrectOrderOfOperations() {
//        // Given
//        when(bookingRepository.findExpiredPendingBookings(any(Instant.class))).thenReturn(List.of(expiredBooking));
//        when(invoiceRepository.findByBookingId(expiredBooking.getId().toString()))
//                .thenReturn(Optional.of(invoice));
//        when(paymentRepository.findByInvoiceId(invoice.getId())).thenReturn(List.of(payment));
//
//        // When
//        expirationService.expireBookings();
//
//        // Then - Verify order of operations
//        var inOrder = inOrder(
//                bookingRepository, screeningSeatRepository, invoiceRepository, paymentRepository, bookingRepository);
//
//        inOrder.verify(bookingRepository).findExpiredPendingBookings(any(Instant.class));
//        inOrder.verify(screeningSeatRepository).releaseSeatsByBooking(anyString());
//        inOrder.verify(invoiceRepository).findByBookingId(anyString());
//        inOrder.verify(invoiceRepository).save(any(Invoice.class));
//        inOrder.verify(paymentRepository).findByInvoiceId(anyString());
//        inOrder.verify(paymentRepository).save(any(Payment.class));
//        inOrder.verify(bookingRepository).saveAll(anyList());
//    }
// }
