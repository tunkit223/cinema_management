package com.theatermgnt.theatermgnt.booking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.theatermgnt.theatermgnt.account.entity.Account;
import com.theatermgnt.theatermgnt.account.repository.AccountRepository;
import com.theatermgnt.theatermgnt.authentication.enums.AccountType;
import com.theatermgnt.theatermgnt.booking.dto.request.CreateBookingRequest;
import com.theatermgnt.theatermgnt.booking.dto.request.DiscountPointRequest;
import com.theatermgnt.theatermgnt.booking.dto.response.BookingSummaryResponse;
import com.theatermgnt.theatermgnt.booking.dto.response.CreateBookingResponse;
import com.theatermgnt.theatermgnt.booking.entity.Booking;
import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;
import com.theatermgnt.theatermgnt.booking.mapper.BookingMapper;
import com.theatermgnt.theatermgnt.booking.mapper.BookingSummaryMapper;
import com.theatermgnt.theatermgnt.booking.repository.BookingRepository;
import com.theatermgnt.theatermgnt.bookingCombo.repository.BookingComboRepository;
import com.theatermgnt.theatermgnt.common.enums.DayType;
import com.theatermgnt.theatermgnt.common.enums.MovieStatus;
import com.theatermgnt.theatermgnt.common.enums.TimeSlot;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
import com.theatermgnt.theatermgnt.customer.dto.response.CustomerLoyaltyPointsResponse;
import com.theatermgnt.theatermgnt.customer.entity.Customer;
import com.theatermgnt.theatermgnt.customer.repository.CustomerRepository;
import com.theatermgnt.theatermgnt.customer.service.CustomerService;
import com.theatermgnt.theatermgnt.movie.entity.Movie;
import com.theatermgnt.theatermgnt.movie.service.MovieService;
import com.theatermgnt.theatermgnt.priceConfig.repository.PriceConfigRepository;
import com.theatermgnt.theatermgnt.screening.entity.Screening;
import com.theatermgnt.theatermgnt.screening.repository.ScreeningRepository;
import com.theatermgnt.theatermgnt.screeningSeat.entity.ScreeningSeat;
import com.theatermgnt.theatermgnt.screeningSeat.enums.ScreeningSeatStatus;
import com.theatermgnt.theatermgnt.screeningSeat.repository.ScreeningSeatRepository;
import com.theatermgnt.theatermgnt.seat.entity.Seat;
import com.theatermgnt.theatermgnt.seat.mapper.SeatMapper;
import com.theatermgnt.theatermgnt.seatType.entity.SeatType;
import com.theatermgnt.theatermgnt.ticket.service.TicketService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BookingComboRepository bookingComboRepository;

    @Mock
    private ScreeningSeatRepository screeningSeatRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PriceConfigRepository priceConfigRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingSummaryMapper bookingSummaryMapper;

    @Mock
    private SeatMapper seatMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MovieService movieService;

    @Mock
    private CustomerService customerService;

    @Mock
    private DiscountService discountService;

    @Mock
    private TicketService ticketService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private CreateBookingRequest createBookingRequest;
    private Screening screening;
    private Movie movie;
    private ScreeningSeat screeningSeat;
    private Seat seat;
    private SeatType seatType;
    private Customer customer;
    private Account account;
    private Booking booking;

    @BeforeEach
    void setUp() {
        // Setup movie
        movie = new Movie();
        movie.setId("movie1");
        movie.setTitle("Test Movie");
        movie.setStatus(MovieStatus.now_showing);

        // Setup screening
        screening = new Screening();
        screening.setId("screening1");
        screening.setMovie(movie);
        screening.setStartTime(LocalDateTime.now().plusDays(1));

        // Setup seat type
        seatType = new SeatType();
        seatType.setId("seatType1");
        seatType.setBasePriceModifier(BigDecimal.valueOf(100000));

        // Setup seat
        seat = new Seat();
        seat.setId("seat1");
        seat.setRowChair("A");
        seat.setSeatNumber(1);
        seat.setSeatType(seatType);

        // Setup screening seat
        screeningSeat = new ScreeningSeat();
        screeningSeat.setId("screeningSeat1");
        screeningSeat.setScreening(screening);
        screeningSeat.setSeat(seat);
        screeningSeat.setStatus(ScreeningSeatStatus.AVAILABLE);

        // Setup customer
        account = new Account();
        account.setId("account1");
        account.setEmail("test@example.com");
        account.setAccountType(AccountType.CUSTOMER);

        customer = new Customer();
        customer.setId("customer1");
        customer.setAccount(account);
        customer.setFirstName("John");
        customer.setLastName("Doe");

        // Setup booking
        booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setScreening(screening);
        booking.setCustomer(customer);
        booking.setStatus(BookingStatus.PENDING);
        booking.setSubtotal(BigDecimal.valueOf(100000));
        booking.setDiscount(BigDecimal.ZERO);
        booking.setTotalAmount(BigDecimal.valueOf(100000));
        booking.setCreatedAt(Instant.now());
        booking.setExpiredAt(Instant.now().plusSeconds(600));

        // Setup create booking request
        createBookingRequest = new CreateBookingRequest();
        createBookingRequest.setScreeningId("screening1");
        createBookingRequest.setScreeningSeatIds(List.of("screeningSeat1"));
        createBookingRequest.setCustomerId("customer1");
    }

    // ================= CREATE BOOKING TESTS =================

    @Test
    void createBooking_success_withExistingCustomer() {
        // Given
        when(screeningRepository.findById("screening1")).thenReturn(Optional.of(screening));
        when(customerRepository.findById("customer1")).thenReturn(Optional.of(customer));
        when(screeningSeatRepository.findByScreeningId("screening1")).thenReturn(List.of(screeningSeat));
        when(screeningSeatRepository.lockSeats(anyList(), any(Instant.class))).thenReturn(1);
        when(screeningSeatRepository.findAllById(anyList())).thenReturn(List.of(screeningSeat));
        when(priceConfigRepository.getPriceBySeatTypeIdAndDayTypeAndTimeSlot(
                        anyString(), any(DayType.class), any(TimeSlot.class)))
                .thenReturn(null);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            if (b.getId() == null) {
                b.setId(UUID.randomUUID());
            }
            return b;
        });

        CreateBookingResponse mockResponse = new CreateBookingResponse();
        mockResponse.setId(String.valueOf(booking.getId()));
        when(bookingMapper.toCreateBookingResponse(any(Booking.class))).thenReturn(mockResponse);

        // When
        CreateBookingResponse response = bookingService.createBooking(createBookingRequest);

        // Then
        assertNotNull(response);
        verify(screeningSeatRepository).lockSeats(anyList(), any(Instant.class));
        verify(bookingRepository).saveAndFlush(any(Booking.class));
        verify(screeningSeatRepository).saveAll(anyList());
    }

    @Test
    void createBooking_throwsException_whenNoSeatsSelected() {
        // Given
        createBookingRequest.setScreeningSeatIds(List.of());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(createBookingRequest));
    }

    @Test
    void createBooking_throwsException_whenTooManySeats() {
        // Given
        List<String> tooManySeats = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            tooManySeats.add("seat" + i);
        }
        createBookingRequest.setScreeningSeatIds(tooManySeats);
        when(screeningRepository.findById("screening1")).thenReturn(Optional.of(screening));

        // When & Then
        AppException exception =
                assertThrows(AppException.class, () -> bookingService.createBooking(createBookingRequest));
        assertEquals(ErrorCode.BOOKING_EXCEED_SEAT_LIMIT, exception.getErrorCode());
    }

    @Test
    void createBooking_throwsException_whenScreeningNotFound() {
        // Given
        when(screeningRepository.findById("screening1")).thenReturn(Optional.empty());

        // When & Then
        AppException exception =
                assertThrows(AppException.class, () -> bookingService.createBooking(createBookingRequest));
        assertEquals(ErrorCode.SCREENING_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void createBooking_throwsException_whenMovieArchived() {
        // Given
        movie.setStatus(MovieStatus.archived);
        when(screeningRepository.findById("screening1")).thenReturn(Optional.of(screening));

        // When & Then
        AppException exception =
                assertThrows(AppException.class, () -> bookingService.createBooking(createBookingRequest));
        assertEquals(ErrorCode.MOVIE_ALREADY_ENDED, exception.getErrorCode());
    }

    @Test
    void createBooking_throwsException_whenSeatsNotAvailable() {
        // Given
        when(screeningRepository.findById("screening1")).thenReturn(Optional.of(screening));
        when(customerRepository.findById("customer1")).thenReturn(Optional.of(customer));
        when(screeningSeatRepository.findByScreeningId("screening1")).thenReturn(List.of(screeningSeat));
        when(screeningSeatRepository.lockSeats(anyList(), any(Instant.class))).thenReturn(0);

        // When & Then
        AppException exception =
                assertThrows(AppException.class, () -> bookingService.createBooking(createBookingRequest));
        assertEquals(ErrorCode.SCREENING_SEATS_NOT_AVAILABLE, exception.getErrorCode());
    }

    @Test
    void createBooking_success_createNewCustomer() {
        // Given
        createBookingRequest.setCustomerId(null);
        createBookingRequest.setEmail("newuser@example.com");
        createBookingRequest.setFirstName("Jane");
        createBookingRequest.setLastName("Smith");

        when(screeningRepository.findById("screening1")).thenReturn(Optional.of(screening));
        when(accountRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(customerRepository.findByAccountId(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(screeningSeatRepository.findByScreeningId("screening1")).thenReturn(List.of(screeningSeat));
        when(screeningSeatRepository.lockSeats(anyList(), any(Instant.class))).thenReturn(1);
        when(screeningSeatRepository.findAllById(anyList())).thenReturn(List.of(screeningSeat));
        when(priceConfigRepository.getPriceBySeatTypeIdAndDayTypeAndTimeSlot(
                        anyString(), any(DayType.class), any(TimeSlot.class)))
                .thenReturn(null);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            if (b.getId() == null) {
                b.setId(UUID.randomUUID());
            }
            return b;
        });

        CreateBookingResponse mockResponse = new CreateBookingResponse();
        when(bookingMapper.toCreateBookingResponse(any(Booking.class))).thenReturn(mockResponse);

        // When
        CreateBookingResponse response = bookingService.createBooking(createBookingRequest);

        // Then
        assertNotNull(response);
        verify(accountRepository).save(any(Account.class));
        verify(customerRepository).save(any(Customer.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any());
    }

    // ================= GET BOOKING SUMMARY TESTS =================

    @Test
    void getBookingSummary_success() {
        // Given
        UUID bookingId = booking.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(screeningSeatRepository.findByBooking(bookingId.toString())).thenReturn(List.of(screeningSeat));
        when(bookingComboRepository.findByBookingId(bookingId.toString())).thenReturn(List.of());
        when(movieService.getMovieById(anyString())).thenReturn(null);
        when(seatMapper.toSeatResponse(any())).thenReturn(null);

        BookingSummaryResponse mockResponse = new BookingSummaryResponse();
        when(bookingSummaryMapper.toSummaryResponse(any(), any(), any(), any())).thenReturn(mockResponse);

        // When
        BookingSummaryResponse response = bookingService.getBookingSummary(bookingId);

        // Then
        assertNotNull(response);
        verify(bookingRepository).findById(bookingId);
        verify(screeningSeatRepository).findByBooking(bookingId.toString());
    }

    @Test
    void getBookingSummary_throwsException_whenBookingNotFound() {
        // Given
        UUID bookingId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> bookingService.getBookingSummary(bookingId));
        assertEquals(ErrorCode.BOOKING_NOT_EXISTED, exception.getErrorCode());
    }

    // ================= REDEEM POINTS TESTS =================

    @Test
    void redeemPoints_success() {
        // Given
        UUID bookingId = booking.getId();
        DiscountPointRequest request = new DiscountPointRequest();
        request.setPointsToRedeem(10000);

        CustomerLoyaltyPointsResponse loyaltyPoints = new CustomerLoyaltyPointsResponse();
        loyaltyPoints.setLoyaltyPoints(50000); // Customer has enough points

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(customerService.getLoyaltyPoints(customer.getId())).thenReturn(loyaltyPoints);
        when(discountService.applyDiscounts(any(Booking.class), anyInt())).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(screeningSeatRepository.findByBooking(bookingId.toString())).thenReturn(List.of(screeningSeat));
        when(bookingComboRepository.findByBookingId(bookingId.toString())).thenReturn(List.of());
        when(movieService.getMovieById(anyString())).thenReturn(null);
        when(seatMapper.toSeatResponse(any())).thenReturn(null);

        BookingSummaryResponse mockResponse = new BookingSummaryResponse();
        when(bookingSummaryMapper.toSummaryResponse(any(), any(), any(), any())).thenReturn(mockResponse);

        // When
        BookingSummaryResponse response = bookingService.redeemPoints(bookingId, request);

        // Then
        assertNotNull(response);
        verify(discountService).applyDiscounts(any(Booking.class), eq(10000));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void redeemPoints_throwsException_whenBookingNotPending() {
        // Given
        booking.setStatus(BookingStatus.PAID);
        UUID bookingId = booking.getId();
        DiscountPointRequest request = new DiscountPointRequest();
        request.setPointsToRedeem(10000);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(IllegalStateException.class, () -> bookingService.redeemPoints(bookingId, request));
    }

    @Test
    void redeemPoints_throwsException_whenExceedsMaxDiscount() {
        // Given
        UUID bookingId = booking.getId();
        DiscountPointRequest request = new DiscountPointRequest();
        request.setPointsToRedeem(60000); // More than 50% of 100,000

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookingService.redeemPoints(bookingId, request));
    }

    // ================= CANCEL BOOKING TESTS =================

    @Test
    void cancelBooking_success() {
        // Given
        UUID bookingId = booking.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);

        // When
        bookingService.cancelBooking(bookingId);

        // Then
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).saveAndFlush(captor.capture());
        assertEquals(BookingStatus.CANCELLED, captor.getValue().getStatus());
        verify(screeningSeatRepository).releaseSeatsByBooking(bookingId.toString());
    }

    @Test
    void cancelBooking_throwsException_whenBookingNotFound() {
        // Given
        UUID bookingId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> bookingService.cancelBooking(bookingId));
        assertEquals(ErrorCode.BOOKING_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void cancelBooking_throwsException_whenBookingNotPending() {
        // Given
        booking.setStatus(BookingStatus.PAID);
        UUID bookingId = booking.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(bookingId));
    }

    // ================= CONFIRM PAYMENT TESTS =================

    @Test
    void confirmBookingPayment_success() {
        // Given
        String bookingId = booking.getId().toString();
        when(bookingRepository.findById(UUID.fromString(bookingId))).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(discountService.calculateEarnedPoints(any(BigDecimal.class))).thenReturn(100);
        when(discountService.caculateDiscountPoints(any(BigDecimal.class))).thenReturn(0);

        // When
        bookingService.confirmBookingPayment(bookingId);

        // Then
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(BookingStatus.PAID, captor.getValue().getStatus());
        verify(screeningSeatRepository).markSeatsAsSoldByBooking(bookingId);
        verify(ticketService).createTickets(UUID.fromString(bookingId));
        verify(customerService).addLoyaltyPoints(customer.getId(), 100);
    }

    @Test
    void confirmBookingPayment_success_withoutCustomer() {
        // Given
        booking.setCustomer(null);
        String bookingId = booking.getId().toString();
        when(bookingRepository.findById(UUID.fromString(bookingId))).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // When
        bookingService.confirmBookingPayment(bookingId);

        // Then
        verify(ticketService).createTickets(UUID.fromString(bookingId));
        verify(customerService, never()).addLoyaltyPoints(anyString(), anyInt());
    }

    @Test
    void confirmBookingPayment_throwsException_whenBookingNotFound() {
        // Given
        String bookingId = UUID.randomUUID().toString();
        when(bookingRepository.findById(UUID.fromString(bookingId))).thenReturn(Optional.empty());

        // When & Then
        AppException exception =
                assertThrows(AppException.class, () -> bookingService.confirmBookingPayment(bookingId));
        assertEquals(ErrorCode.BOOKING_NOT_EXISTED, exception.getErrorCode());
    }

    // ================= REFUND BOOKING TESTS =================

    @Test
    void refundBooking_success() {
        // Given
        booking.setStatus(BookingStatus.PAID);
        String bookingId = booking.getId().toString();
        when(bookingRepository.findById(UUID.fromString(bookingId))).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(discountService.calculateEarnedPoints(any(BigDecimal.class))).thenReturn(100);
        when(discountService.caculateDiscountPoints(any(BigDecimal.class))).thenReturn(0);

        // When
        bookingService.refundBooking(bookingId);

        // Then
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(BookingStatus.REFUNDED, captor.getValue().getStatus());
        verify(customerService).addLoyaltyPoints(customer.getId(), -100);
        verify(screeningSeatRepository).releaseSeatsByBooking(bookingId);
    }

    @Test
    void refundBooking_throwsException_whenBookingNotFound() {
        // Given
        String bookingId = UUID.randomUUID().toString();
        when(bookingRepository.findById(UUID.fromString(bookingId))).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class, () -> bookingService.refundBooking(bookingId));
        assertEquals(ErrorCode.BOOKING_NOT_EXISTED, exception.getErrorCode());
    }

    // ================= ORPHAN SEAT VALIDATION TESTS =================

    @Test
    void createBooking_throwsException_whenOrphanSeatViolation() {
        // Given - Create seats in a row where selecting certain seats would create orphans
        Seat seat1 = createSeat("seat1", "A", 1);
        Seat seat2 = createSeat("seat2", "A", 2);
        Seat seat3 = createSeat("seat3", "A", 3);
        Seat seat4 = createSeat("seat4", "A", 4);

        ScreeningSeat ss1 = createScreeningSeat("ss1", seat1, ScreeningSeatStatus.AVAILABLE);
        ScreeningSeat ss2 = createScreeningSeat("ss2", seat2, ScreeningSeatStatus.AVAILABLE);
        ScreeningSeat ss3 = createScreeningSeat("ss3", seat3, ScreeningSeatStatus.AVAILABLE);
        ScreeningSeat ss4 = createScreeningSeat("ss4", seat4, ScreeningSeatStatus.SOLD);

        createBookingRequest.setScreeningSeatIds(List.of("ss1", "ss3")); // Selecting 1 and 3, leaving 2 orphaned

        when(screeningRepository.findById("screening1")).thenReturn(Optional.of(screening));
        when(customerRepository.findById("customer1")).thenReturn(Optional.of(customer));
        when(screeningSeatRepository.findByScreeningId("screening1")).thenReturn(List.of(ss1, ss2, ss3, ss4));
        when(screeningSeatRepository.findAllById(List.of("ss1", "ss3"))).thenReturn(List.of(ss1, ss3));
        when(screeningSeatRepository.lockSeats(anyList(), any(Instant.class))).thenReturn(2);
        when(priceConfigRepository.getPriceBySeatTypeIdAndDayTypeAndTimeSlot(
                        anyString(), any(DayType.class), any(TimeSlot.class)))
                .thenReturn(null);

        // When & Then
        AppException exception =
                assertThrows(AppException.class, () -> bookingService.createBooking(createBookingRequest));
        assertEquals(ErrorCode.ORPHAN_SEAT_VIOLATION, exception.getErrorCode());
    }

    // ================= HELPER METHODS =================

    private Seat createSeat(String id, String row, int number) {
        Seat s = new Seat();
        s.setId(java.util.UUID.randomUUID().toString());
        s.setRowChair(row);
        s.setSeatNumber(number);
        s.setSeatType(seatType);
        return s;
    }

    private ScreeningSeat createScreeningSeat(String id, Seat seat, ScreeningSeatStatus status) {
        ScreeningSeat ss = new ScreeningSeat();
        ss.setId(id); // Use the provided id parameter
        ss.setSeat(seat);
        ss.setScreening(screening);
        ss.setStatus(status);
        return ss;
    }
}
