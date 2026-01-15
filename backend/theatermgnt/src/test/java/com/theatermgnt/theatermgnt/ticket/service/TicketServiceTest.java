 package com.theatermgnt.theatermgnt.ticket.service;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.ArgumentMatchers.anyList;
 import static org.mockito.ArgumentMatchers.anyString;
 import static org.mockito.Mockito.*;

 import java.math.BigDecimal;
 import java.time.Instant;
 import java.time.LocalDateTime;
 import java.util.List;
 import java.util.Optional;
 import java.util.UUID;

 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.ArgumentMatchers;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.junit.jupiter.MockitoExtension;
 import org.mockito.junit.jupiter.MockitoSettings;
 import org.mockito.quality.Strictness;
 import org.springframework.context.ApplicationEventPublisher;

 import com.theatermgnt.theatermgnt.booking.entity.Booking;
 import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;
 import com.theatermgnt.theatermgnt.booking.repository.BookingRepository;
 import com.theatermgnt.theatermgnt.bookingCombo.dto.response.ComboCheckInResponse;
 import com.theatermgnt.theatermgnt.bookingCombo.entity.BookingCombo;
 import com.theatermgnt.theatermgnt.bookingCombo.repository.BookingComboRepository;
 import com.theatermgnt.theatermgnt.bookingCombo.service.BookingComboService;
 import com.theatermgnt.theatermgnt.common.exception.AppException;
 import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
 import com.theatermgnt.theatermgnt.customer.entity.Customer;
 import com.theatermgnt.theatermgnt.screening.entity.Screening;
 import com.theatermgnt.theatermgnt.screeningSeat.dto.response.ScreeningSeatResponse;
 import com.theatermgnt.theatermgnt.screeningSeat.entity.ScreeningSeat;
 import com.theatermgnt.theatermgnt.screeningSeat.mapper.ScreeningSeatMapper;
 import com.theatermgnt.theatermgnt.screeningSeat.repository.ScreeningSeatRepository;
 import com.theatermgnt.theatermgnt.screeningSeat.service.ScreeningSeatService;
 import com.theatermgnt.theatermgnt.seat.entity.Seat;
 import com.theatermgnt.theatermgnt.ticket.dto.request.ComboUse;
 import com.theatermgnt.theatermgnt.ticket.dto.request.TicketCheckInRequest;
 import com.theatermgnt.theatermgnt.ticket.dto.response.TicketCheckInResponse;
 import com.theatermgnt.theatermgnt.ticket.dto.response.TicketCheckInViewResponse;
 import com.theatermgnt.theatermgnt.ticket.dto.response.TicketResponse;
 import com.theatermgnt.theatermgnt.ticket.entity.Ticket;
 import com.theatermgnt.theatermgnt.ticket.enums.TicketStatus;
 import com.theatermgnt.theatermgnt.ticket.mapper.TicketMapper;
 import com.theatermgnt.theatermgnt.ticket.repository.TicketRepository;

 @ExtendWith(MockitoExtension.class)
 @MockitoSettings(strictness = Strictness.LENIENT)
 class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingComboRepository bookingComboRepository;

    @Mock
    private ScreeningSeatRepository screeningSeatRepository;

    @Mock
    private ScreeningSeatService screeningSeatService;

    @Mock
    private BookingComboService bookingComboService;

    @Mock
    private TicketCodeGenerator ticketCodeGenerator;

    @Mock
    private QrGenerator qrGenerator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Mock
    private ScreeningSeatMapper screeningSeatMapper;

    private UUID bookingId;
    private UUID ticketId;
    private String ticketCode;
    private String qrContent;
    private String customerId;
    private Booking booking;
    private Ticket ticket;
    private TicketResponse ticketResponse;
    private ScreeningSeat screeningSeat;
    private ScreeningSeatResponse screeningSeatResponse;
    private Seat seat;
    private Screening screening;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
        ticketCode = "TICKET123456";
        qrContent = "TICKET123456|QR_DATA";
        customerId = "customer1";

        // Setup Seat
        seat = new Seat();
        seat.setId(String.valueOf(UUID.randomUUID()));
        seat.setRowChair("A");
        seat.setSeatNumber(1);

        // Setup Screening
        screening = new Screening();
        screening.setId(String.valueOf(UUID.randomUUID()));
        screening.setEndTime(LocalDateTime.now().plusDays(1));

        // Setup Customer
        Customer customer = new Customer();
        customer.setId(customerId);

        // Setup Booking
        booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.PAID);
        booking.setScreening(screening);
        booking.setCustomer(customer);

        // Setup ScreeningSeat
        screeningSeat = new ScreeningSeat();
        screeningSeat.setId(String.valueOf(UUID.randomUUID()));
        screeningSeat.setSeat(seat);
        screeningSeat.setBooking(String.valueOf(bookingId));

        // Setup Ticket
        ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setBooking(booking);
        ticket.setScreeningSeat(screeningSeat);
        ticket.setTicketCode(ticketCode);
        ticket.setQrContent(qrContent);
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setExpiresAt(Instant.now().plusSeconds(86400));
        ticket.setSeatName("A1");
        ticket.setPrice(new BigDecimal("150000"));

        // Setup TicketResponse
        ticketResponse = new TicketResponse();
        ticketResponse.setId(ticketId);
        ticketResponse.setTicketCode(ticketCode);
        ticketResponse.setStatus(TicketStatus.ACTIVE);
        ticketResponse.setSeatName("A1");

        // Setup ScreeningSeatResponse
        screeningSeatResponse = new ScreeningSeatResponse();
        screeningSeatResponse.setPrice(new BigDecimal("150000"));
    }

    // ================= GET TICKETS BY BOOKING TESTS =================

    @Test
    void getTicketsByBooking_success() {
        // Given
        when(ticketRepository.findAllByBookingId(bookingId)).thenReturn(List.of(ticket));
        when(ticketMapper.toResponse(ticket)).thenReturn(ticketResponse);

        // When
        List<TicketResponse> responses = ticketService.getTicketsByBooking(bookingId);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(ticketCode, responses.get(0).getTicketCode());
    }

    @Test
    void getTicketsByBooking_returnsEmptyList() {
        // Given
        when(ticketRepository.findAllByBookingId(bookingId)).thenReturn(List.of());

        // When
        List<TicketResponse> responses = ticketService.getTicketsByBooking(bookingId);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void getTicketsByBooking_multipleTickets() {
        // Given
        Ticket ticket2 = new Ticket();
        ticket2.setId(UUID.randomUUID());
        ticket2.setTicketCode("TICKET789012");
        ticket2.setStatus(TicketStatus.ACTIVE);

        TicketResponse response2 = new TicketResponse();
        response2.setId(ticket2.getId());
        response2.setTicketCode("TICKET789012");

        when(ticketRepository.findAllByBookingId(bookingId)).thenReturn(List.of(ticket, ticket2));
        when(ticketMapper.toResponse(ticket)).thenReturn(ticketResponse);
        when(ticketMapper.toResponse(ticket2)).thenReturn(response2);

        // When
        List<TicketResponse> responses = ticketService.getTicketsByBooking(bookingId);

        // Then
        assertEquals(2, responses.size());
    }

    // ================= CHECK IN BY QR TESTS =================

    @Test
    void checkInByQr_success() {
        // Given
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));

        // When
        TicketCheckInResponse response = ticketService.checkInByQr(qrContent);

        // Then
        assertNotNull(response);
        assertEquals(ticketCode, response.getTicketCode());
        assertEquals(TicketStatus.USED, response.getStatus());
        assertNotNull(response.getUsedAt());
        assertEquals("Check-in successful", response.getMessage());
    }

    @Test
    void checkInByQr_throwsException_whenTicketNotFound() {
        // Given
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> ticketService.checkInByQr(qrContent));
        assertEquals(ErrorCode.TICKET_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void checkInByQr_returnsNotActiveStatus() {
        // Given
        ticket.setStatus(TicketStatus.USED);
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));

        // When
        TicketCheckInResponse response = ticketService.checkInByQr(qrContent);

        // Then
        assertEquals(TicketStatus.USED, response.getStatus());
        assertEquals("Ticket is not active", response.getMessage());
    }

    @Test
    void checkInByQr_handlesExpiredTicket() {
        // Given
        ticket.setExpiresAt(Instant.now().minusSeconds(3600));
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));

        // When
        TicketCheckInResponse response = ticketService.checkInByQr(qrContent);

        // Then
        assertEquals(TicketStatus.EXPIRED, response.getStatus());
        assertEquals("Ticket expired", response.getMessage());
    }

    @Test
    void checkInByQr_extractsTicketCodeCorrectly() {
        // Given
        String complexQrContent = "TICKET123456|EXTRA|DATA|MORE";
        when(ticketRepository.findByTicketCode("TICKET123456")).thenReturn(Optional.of(ticket));

        // When
        ticketService.checkInByQr(complexQrContent);

        // Then
        verify(ticketRepository).findByTicketCode("TICKET123456");
    }

    // ================= CREATE TICKETS TESTS =================

    @Test
    void createTickets_success() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(screeningSeatRepository.findByBooking(bookingId.toString())).thenReturn(List.of(screeningSeat));
        when(ticketCodeGenerator.generate()).thenReturn(ticketCode);
        when(ticketRepository.existsByTicketCode(ticketCode)).thenReturn(false);
        when(qrGenerator.generateQrContent(ticketCode)).thenReturn(qrContent);
        when(screeningSeatService.getScreeningSeat(screeningSeat.getId())).thenReturn(screeningSeatResponse);
        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(ticket));

        // When
        List<Ticket> tickets = ticketService.createTickets(bookingId);

        // Then
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        assertEquals(ticketCode, tickets.get(0).getTicketCode());
        assertEquals(TicketStatus.ACTIVE, tickets.get(0).getStatus());
    }

    @Test
    void createTickets_throwsException_whenBookingNotFound() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> ticketService.createTickets(bookingId));
        assertEquals(ErrorCode.BOOKING_NOT_EXISTED, exception.getErrorCode());
        verify(ticketRepository, never()).saveAll(anyList());
    }

    @Test
    void createTickets_throwsException_whenBookingNotPaid() {
        // Given
        booking.setStatus(BookingStatus.PENDING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(IllegalStateException.class, () -> ticketService.createTickets(bookingId));
    }

    @Test
    void createTickets_throwsException_whenNoSeats() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(screeningSeatRepository.findByBooking(bookingId.toString())).thenReturn(List.of());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> ticketService.createTickets(bookingId));
        assertEquals(ErrorCode.SCREENING_SEAT_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void createTickets_generatesUniqueCode() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(screeningSeatRepository.findByBooking(bookingId.toString())).thenReturn(List.of(screeningSeat));
        when(ticketCodeGenerator.generate()).thenReturn(ticketCode, "TICKET123456_2");
        when(ticketRepository.existsByTicketCode(ticketCode)).thenReturn(true, false);
        when(qrGenerator.generateQrContent(anyString())).thenReturn(qrContent);
        when(screeningSeatService.getScreeningSeat(screeningSeat.getId())).thenReturn(screeningSeatResponse);
        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(ticket));

        // When
        ticketService.createTickets(bookingId);

        // Then
        verify(ticketCodeGenerator, times(2)).generate();
        verify(ticketRepository, times(2)).existsByTicketCode(anyString());
    }

    @Test
    void createTickets_multipleSeats() {
        // Given
        ScreeningSeat seat2 = new ScreeningSeat();
        seat2.setId(String.valueOf(UUID.randomUUID()));
        seat2.setSeat(new Seat());
        seat2.getSeat().setRowChair("B");
        seat2.getSeat().setSeatNumber(2);

        Ticket ticket2 = new Ticket();
        ticket2.setId(UUID.randomUUID());
        ticket2.setTicketCode("TICKET789");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(screeningSeatRepository.findByBooking(bookingId.toString())).thenReturn(List.of(screeningSeat, seat2));
        when(ticketCodeGenerator.generate()).thenReturn(ticketCode, "TICKET789");
        when(ticketRepository.existsByTicketCode(anyString())).thenReturn(false);
        when(qrGenerator.generateQrContent(anyString())).thenReturn(qrContent);
        when(screeningSeatService.getScreeningSeat(String.valueOf(ArgumentMatchers.any(UUID.class))))
                .thenReturn(screeningSeatResponse);
        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(ticket, ticket2));

        // When
        List<Ticket> tickets = ticketService.createTickets(bookingId);

        // Then
        assertEquals(2, tickets.size());
    }

    @Test
    void createTickets_publishesEventWhenCustomerExists() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(screeningSeatRepository.findByBooking(bookingId.toString())).thenReturn(List.of(screeningSeat));
        when(ticketCodeGenerator.generate()).thenReturn(ticketCode);
        when(ticketRepository.existsByTicketCode(ticketCode)).thenReturn(false);
        when(qrGenerator.generateQrContent(ticketCode)).thenReturn(qrContent);
        when(screeningSeatService.getScreeningSeat(screeningSeat.getId())).thenReturn(screeningSeatResponse);
        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(ticket));

        // When
        ticketService.createTickets(bookingId);

        // Then
        verify(eventPublisher).publishEvent(any());
    }

    // ================= GET TICKET BY CODE TESTS =================

    @Test
    void getTicketByCode_success() {
        // Given
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));

        // When
        Ticket result = ticketService.getTicketByCode(ticketCode);

        // Then
        assertNotNull(result);
        assertEquals(ticketCode, result.getTicketCode());
    }

    @Test
    void getTicketByCode_throwsException_whenNotFound() {
        // Given
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> ticketService.getTicketByCode(ticketCode));
        assertEquals(ErrorCode.TICKET_NOT_EXISTED, exception.getErrorCode());
    }

    // ================= GET TICKET CHECK IN VIEW TESTS =================

    @Test
    void getTicketCheckInViewByCode_success() {
        // Given
        ComboCheckInResponse comboResponse = new ComboCheckInResponse();
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));
        when(bookingComboService.getCombos(bookingId)).thenReturn(List.of(comboResponse));
        when(ticketMapper.toResponse(ticket)).thenReturn(ticketResponse);

        // When
        TicketCheckInViewResponse response = ticketService.getTicketCheckInViewByCode(ticketCode);

        // Then
        assertNotNull(response);
        assertEquals(ticketResponse.getTicketCode(), response.getTicket().getTicketCode());
        assertEquals(1, response.getComboCheckIn().size());
    }

    @Test
    void getTicketCheckInViewByCode_throwsException_whenTicketNotFound() {
        // Given
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.empty());

        // When & Then
        AppException exception =
                assertThrows(AppException.class, () -> ticketService.getTicketCheckInViewByCode(ticketCode));
        assertEquals(ErrorCode.TICKET_NOT_EXISTED, exception.getErrorCode());
    }

    // ================= GET TICKETS BY CUSTOMER TESTS =================

    @Test
    void getTicketsByCustomerId_success() {
        // Given
        when(ticketRepository.findByBooking_Customer_IdOrderByCreatedAtDesc(customerId))
                .thenReturn(List.of(ticket));

        // When
        List<Ticket> tickets = ticketService.getTicketsByCustomerId(customerId);

        // Then
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
    }

    @Test
    void getTicketsByCustomerId_returnsEmptyList() {
        // Given
        when(ticketRepository.findByBooking_Customer_IdOrderByCreatedAtDesc(customerId))
                .thenReturn(List.of());

        // When
        List<Ticket> tickets = ticketService.getTicketsByCustomerId(customerId);

        // Then
        assertTrue(tickets.isEmpty());
    }

    @Test
    void getTicketsByCustomerId_returnsOrderedByCreatedAt() {
        // Given
        Ticket oldTicket = new Ticket();
        oldTicket.setId(UUID.randomUUID());
        oldTicket.setTicketCode("OLD_TICKET");

        when(ticketRepository.findByBooking_Customer_IdOrderByCreatedAtDesc(customerId))
                .thenReturn(List.of(ticket, oldTicket));

        // When
        List<Ticket> tickets = ticketService.getTicketsByCustomerId(customerId);

        // Then
        assertEquals(2, tickets.size());
        assertEquals(ticketCode, tickets.get(0).getTicketCode());
    }

    // ================= EXPIRE TICKETS TESTS =================

    @Test
    void expireTickets_success() {
        // Given
        List<Ticket> expiredTickets = List.of(ticket);
        when(ticketRepository.findAllByStatusAndExpiresAtBefore(TicketStatus.ACTIVE, any(Instant.class)))
                .thenReturn(expiredTickets);

        // When
        ticketService.expireTickets();

        // Then
        assertEquals(TicketStatus.EXPIRED, ticket.getStatus());
        verify(ticketRepository).saveAll(expiredTickets);
    }

    @Test
    void expireTickets_noTicketsToExpire() {
        // Given
        when(ticketRepository.findAllByStatusAndExpiresAtBefore(TicketStatus.ACTIVE, any(Instant.class)))
                .thenReturn(List.of());

        // When
        ticketService.expireTickets();

        // Then
        verify(ticketRepository, never()).saveAll(anyList());
    }

    @Test
    void expireTickets_multipleTickets() {
        // Given
        Ticket ticket2 = new Ticket();
        ticket2.setId(UUID.randomUUID());
        ticket2.setStatus(TicketStatus.ACTIVE);

        List<Ticket> expiredTickets = List.of(ticket, ticket2);
        when(ticketRepository.findAllByStatusAndExpiresAtBefore(TicketStatus.ACTIVE, any(Instant.class)))
                .thenReturn(expiredTickets);

        // When
        ticketService.expireTickets();

        // Then
        assertEquals(TicketStatus.EXPIRED, ticket.getStatus());
        assertEquals(TicketStatus.EXPIRED, ticket2.getStatus());
        verify(ticketRepository).saveAll(expiredTickets);
    }

    // ================= CHECK IN TICKET TESTS =================

    @Test
    void checkInTicket_success() {
        // Given
        TicketCheckInRequest request = new TicketCheckInRequest();
        request.setTicketCode(ticketCode);
        request.setComboUseList(List.of());

        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));

        // When
        ticketService.checkInTicket(request);

        // Then
        assertEquals(TicketStatus.USED, ticket.getStatus());
        assertNotNull(ticket.getUsedAt());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void checkInTicket_throwsException_whenTicketNotFound() {
        // Given
        TicketCheckInRequest request = new TicketCheckInRequest();
        request.setTicketCode(ticketCode);

        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> ticketService.checkInTicket(request));
        assertEquals(ErrorCode.TICKET_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void checkInTicket_throwsException_whenTicketNotActive() {
        // Given
        ticket.setStatus(TicketStatus.USED);
        TicketCheckInRequest request = new TicketCheckInRequest();
        request.setTicketCode(ticketCode);

        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> ticketService.checkInTicket(request));
        assertEquals(ErrorCode.TICKET_NOT_ACTIVE, exception.getErrorCode());
    }

    @Test
    void checkInTicket_throwsException_whenTicketExpired() {
        // Given
        ticket.setExpiresAt(Instant.now().minusSeconds(3600));
        TicketCheckInRequest request = new TicketCheckInRequest();
        request.setTicketCode(ticketCode);

        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> ticketService.checkInTicket(request));
        assertEquals(ErrorCode.TICKET_EXPIRED, exception.getErrorCode());
        assertEquals(TicketStatus.EXPIRED, ticket.getStatus());
    }

    @Test
    void checkInTicket_withCombos() {
        // Given
        BookingCombo combo = new BookingCombo();
        combo.setId(String.valueOf(UUID.randomUUID()));
        combo.setRemain(5);

        ComboUse comboUse = new ComboUse();
        comboUse.setComboId(combo.getId());
        comboUse.setQuantity(2);

        TicketCheckInRequest request = new TicketCheckInRequest();
        request.setTicketCode(ticketCode);
        request.setComboUseList(List.of(comboUse));

        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));
        when(bookingComboRepository.findById(combo.getId())).thenReturn(Optional.of(combo));

        // When
        ticketService.checkInTicket(request);

        // Then
        assertEquals(3, combo.getRemain());
        assertEquals(TicketStatus.USED, ticket.getStatus());
        verify(bookingComboRepository).save(combo);
    }

    @Test
    void checkInTicket_throwsException_whenInsufficientComboQuantity() {
        // Given
        BookingCombo combo = new BookingCombo();
        combo.setId(String.valueOf(UUID.randomUUID()));
        combo.setRemain(1);

        ComboUse comboUse = new ComboUse();
        comboUse.setComboId(combo.getId());
        comboUse.setQuantity(2);

        TicketCheckInRequest request = new TicketCheckInRequest();
        request.setTicketCode(ticketCode);
        request.setComboUseList(List.of(comboUse));

        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));
        when(bookingComboRepository.findById(combo.getId())).thenReturn(Optional.of(combo));

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> ticketService.checkInTicket(request));
        assertEquals(ErrorCode.INSUFFICIENT_COMBO_QUANTITY, exception.getErrorCode());
    }

    @Test
    void checkInTicket_throwsException_whenComboNotFound() {
        // Given
        ComboUse comboUse = new ComboUse();
        comboUse.setComboId(String.valueOf(UUID.randomUUID()));
        comboUse.setQuantity(1);

        TicketCheckInRequest request = new TicketCheckInRequest();
        request.setTicketCode(ticketCode);
        request.setComboUseList(List.of(comboUse));

        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));
        when(bookingComboRepository.findById(String.valueOf(ArgumentMatchers.any(UUID.class))))
                .thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> ticketService.checkInTicket(request));
        assertEquals(ErrorCode.BOOKING_COMBO_NOT_EXISTED, exception.getErrorCode());
    }

    // ================= EXPIRE TICKETS BY BOOKING ID TESTS =================

    @Test
    void expireTicketsByBookingId_success() {
        // Given
        when(ticketRepository.findByBookingIdAndStatus(bookingId, TicketStatus.ACTIVE))
                .thenReturn(List.of(ticket));

        // When
        ticketService.expireTicketsByBookingId(bookingId);

        // Then
        assertEquals(TicketStatus.EXPIRED, ticket.getStatus());
        verify(ticketRepository).saveAll(List.of(ticket));
    }

    @Test
    void expireTicketsByBookingId_noActiveTickets() {
        // Given
        when(ticketRepository.findByBookingIdAndStatus(bookingId, TicketStatus.ACTIVE))
                .thenReturn(List.of());

        // When
        ticketService.expireTicketsByBookingId(bookingId);

        // Then
        verify(ticketRepository, never()).saveAll(anyList());
    }

    @Test
    void expireTicketsByBookingId_multipleTickets() {
        // Given
        Ticket ticket2 = new Ticket();
        ticket2.setId(UUID.randomUUID());
        ticket2.setStatus(TicketStatus.ACTIVE);

        List<Ticket> activeTickets = List.of(ticket, ticket2);
        when(ticketRepository.findByBookingIdAndStatus(bookingId, TicketStatus.ACTIVE))
                .thenReturn(activeTickets);

        // When
        ticketService.expireTicketsByBookingId(bookingId);

        // Then
        assertEquals(TicketStatus.EXPIRED, ticket.getStatus());
        assertEquals(TicketStatus.EXPIRED, ticket2.getStatus());
        verify(ticketRepository).saveAll(activeTickets);
    }

    // ================= INTEGRATION TESTS =================

    @Test
    void createAndGetTickets() {
        // Given - Create
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(screeningSeatRepository.findByBooking(bookingId.toString())).thenReturn(List.of(screeningSeat));
        when(ticketCodeGenerator.generate()).thenReturn(ticketCode);
        when(ticketRepository.existsByTicketCode(ticketCode)).thenReturn(false);
        when(qrGenerator.generateQrContent(ticketCode)).thenReturn(qrContent);
        when(screeningSeatService.getScreeningSeat(screeningSeat.getId())).thenReturn(screeningSeatResponse);
        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(ticket));

        // When - Create
        List<Ticket> createdTickets = ticketService.createTickets(bookingId);

        // Given - Get
        when(ticketRepository.findAllByBookingId(bookingId)).thenReturn(createdTickets);
        when(ticketMapper.toResponse(ticket)).thenReturn(ticketResponse);

        // When - Get
        List<TicketResponse> responses = ticketService.getTicketsByBooking(bookingId);

        // Then
        assertEquals(1, responses.size());
        assertEquals(ticketCode, responses.get(0).getTicketCode());
    }

    @Test
    void ticketLifecycle_createCheckInExpire() {
        // Given - Create
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(screeningSeatRepository.findByBooking(bookingId.toString())).thenReturn(List.of(screeningSeat));
        when(ticketCodeGenerator.generate()).thenReturn(ticketCode);
        when(ticketRepository.existsByTicketCode(ticketCode)).thenReturn(false);
        when(qrGenerator.generateQrContent(ticketCode)).thenReturn(qrContent);
        when(screeningSeatService.getScreeningSeat(screeningSeat.getId())).thenReturn(screeningSeatResponse);
        when(ticketRepository.saveAll(anyList())).thenReturn(List.of(ticket));

        // When - Create
        ticketService.createTickets(bookingId);

        // Given - Check-in
        TicketCheckInRequest checkInRequest = new TicketCheckInRequest();
        checkInRequest.setTicketCode(ticketCode);
        checkInRequest.setComboUseList(List.of());
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));

        // When - Check-in
        ticketService.checkInTicket(checkInRequest);
        assertEquals(TicketStatus.USED, ticket.getStatus());

        // Given - Expire
        ticket.setStatus(TicketStatus.ACTIVE);
        when(ticketRepository.findByBookingIdAndStatus(bookingId, TicketStatus.ACTIVE))
                .thenReturn(List.of(ticket));

        // When - Expire
        ticketService.expireTicketsByBookingId(bookingId);
        assertEquals(TicketStatus.EXPIRED, ticket.getStatus());
    }

    @Test
    void multipleTicketsCheckIn() {
        // Given
        Ticket ticket2 = new Ticket();
        ticket2.setId(UUID.randomUUID());
        ticket2.setTicketCode("TICKET789");
        ticket2.setStatus(TicketStatus.ACTIVE);
        ticket2.setExpiresAt(Instant.now().plusSeconds(86400));

        // When - Check-in ticket 1
        TicketCheckInRequest request1 = new TicketCheckInRequest();
        request1.setTicketCode(ticketCode);
        request1.setComboUseList(List.of());
        when(ticketRepository.findByTicketCode(ticketCode)).thenReturn(Optional.of(ticket));
        ticketService.checkInTicket(request1);

        // When - Check-in ticket 2
        TicketCheckInRequest request2 = new TicketCheckInRequest();
        request2.setTicketCode("TICKET789");
        request2.setComboUseList(List.of());
        when(ticketRepository.findByTicketCode("TICKET789")).thenReturn(Optional.of(ticket2));
        ticketService.checkInTicket(request2);

        // Then
        assertEquals(TicketStatus.USED, ticket.getStatus());
        assertEquals(TicketStatus.USED, ticket2.getStatus());
    }
 }
