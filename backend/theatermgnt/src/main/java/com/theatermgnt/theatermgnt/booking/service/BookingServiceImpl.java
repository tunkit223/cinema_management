package com.theatermgnt.theatermgnt.booking.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.theatermgnt.theatermgnt.account.entity.Account;
import com.theatermgnt.theatermgnt.account.repository.AccountRepository;
import com.theatermgnt.theatermgnt.authentication.enums.AccountType;
import com.theatermgnt.theatermgnt.booking.dto.request.CreateBookingRequest;
import com.theatermgnt.theatermgnt.booking.dto.response.BookingSummaryResponse;
import com.theatermgnt.theatermgnt.booking.dto.response.CreateBookingResponse;
import com.theatermgnt.theatermgnt.booking.entity.Booking;
import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;
import com.theatermgnt.theatermgnt.booking.mapper.BookingMapper;
import com.theatermgnt.theatermgnt.booking.mapper.BookingSummaryMapper;
import com.theatermgnt.theatermgnt.booking.repository.BookingRepository;
import com.theatermgnt.theatermgnt.bookingCombo.entity.BookingCombo;
import com.theatermgnt.theatermgnt.bookingCombo.repository.BookingComboRepository;
import com.theatermgnt.theatermgnt.common.enums.DayType;
import com.theatermgnt.theatermgnt.common.enums.TimeSlot;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
import com.theatermgnt.theatermgnt.customer.entity.Customer;
import com.theatermgnt.theatermgnt.customer.repository.CustomerRepository;
import com.theatermgnt.theatermgnt.priceConfig.entity.PriceConfig;
import com.theatermgnt.theatermgnt.priceConfig.repository.PriceConfigRepository;
import com.theatermgnt.theatermgnt.screening.entity.Screening;
import com.theatermgnt.theatermgnt.screening.repository.ScreeningRepository;
import com.theatermgnt.theatermgnt.screeningSeat.entity.ScreeningSeat;
import com.theatermgnt.theatermgnt.screeningSeat.repository.ScreeningSeatRepository;
import com.theatermgnt.theatermgnt.seat.entity.Seat;
import com.theatermgnt.theatermgnt.seat.mapper.SeatMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final AccountRepository accountRepository;
    private final BookingComboRepository bookingComboRepository;
    private final ScreeningSeatRepository screeningSeatRepository;
    private final ScreeningRepository screeningRepository;
    private final CustomerRepository customerRepository;
    private final PriceConfigRepository priceConfigRepository;
    private final BookingMapper bookingMapper;
    private final BookingSummaryMapper bookingSummaryMapper;
    private final SeatMapper seatMapper;
    private final PasswordEncoder passwordEncoder;

    private static final Duration HOLD_DURATION = Duration.ofMinutes(10);

    @Override
    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        if (request.getScreeningSeatIds().isEmpty()) {
            throw new IllegalArgumentException("No seats selected for booking");
        }

        Instant now = Instant.now();
        Instant expiredAt = now.plus(HOLD_DURATION);

        int lockedCount = screeningSeatRepository.lockSeats(request.getScreeningSeatIds(), expiredAt);

        if (lockedCount != request.getScreeningSeatIds().size()) {
            throw new IllegalStateException("Some seats are not available");
        }

        List<ScreeningSeat> seats = screeningSeatRepository.findAllById(request.getScreeningSeatIds());
        Customer customer = resolveCustomer(request);
        Screening screening = screeningRepository
                .findById(request.getScreeningId())
                .orElseThrow(() -> new AppException(ErrorCode.SCREENING_NOT_EXISTED));

        // 3. Tạo booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setScreening(screening);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(now);
        booking.setExpiredAt(expiredAt);
        BigDecimal seatSubtotal = calculateSeatSubtotal(seats);
        booking.setSubtotal(seatSubtotal);
        booking.setDiscount(BigDecimal.ZERO);
        booking.setTotalAmount(seatSubtotal);

        bookingRepository.saveAndFlush(booking);

        seats.forEach(seat -> seat.setBooking(booking.getId().toString()));
        screeningSeatRepository.saveAll(seats);

        return bookingMapper.toCreateBookingResponse(booking);
    }

    private Customer resolveCustomer(CreateBookingRequest request) {
        if (request.getCustomerId() != null) {
            return customerRepository
                    .findById(request.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        }

        // Kiểm tra Account tồn tại
        Optional<Account> existingAccount = accountRepository.findByEmail(request.getEmail());
        if (existingAccount.isPresent()) {
            return customerRepository
                    .findByAccountId(existingAccount.get().getId())
                    .orElseGet(() -> createCustomerWithAccount(existingAccount.get(), request));
        }

        String rawPassword = RandomStringUtils.randomAlphanumeric(6);
        Account newAccount = new Account();
        newAccount.setPassword(passwordEncoder.encode(rawPassword));
        newAccount.setEmail(request.getEmail());
        newAccount.setAccountType(AccountType.CUSTOMER);
        newAccount.setIsActive(true);

        accountRepository.save(newAccount);
        Customer savedCustomer = createCustomerWithAccount(newAccount, request);

        // 4. Bắn Event (Async) để gửi SMS/Email, không làm chậm quá trình đặt vé
        //        eventPublisher.publishEvent(new CustomerCreatedEvent(savedCustomer, true));

        return savedCustomer;
    }

    private Customer createCustomerWithAccount(Account account, CreateBookingRequest request) {
        Customer newCustomer = new Customer();
        newCustomer.setAccount(account);
        newCustomer.setFirstName(request.getFirstName());
        newCustomer.setLastName(request.getLastName());

        return customerRepository.save(newCustomer);
    }

    private BigDecimal calculateSeatSubtotal(List<ScreeningSeat> seats) {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (ScreeningSeat seat : seats) {
            Seat s = seat.getSeat();
            Screening screening = seat.getScreening();
            TimeSlot timeSlot = TimeSlot.from(screening.getStartTime().toLocalTime());
            DayType dayType = DayType.from(screening.getStartTime().toLocalDate());
            PriceConfig priceConfig = priceConfigRepository.getPriceBySeatTypeIdAndDayTypeAndTimeSlot(
                    s.getSeatType().getId(), dayType, timeSlot);
            if (priceConfig.getPrice() == null) {
                throw new IllegalStateException("PriceConfig price is null for seatTypeId: "
                        + s.getSeatType().getId()
                        + ", dayType: " + dayType
                        + ", timeSlot: " + timeSlot);
            }
            subTotal = subTotal.add(priceConfig.getPrice());
        }
        return subTotal;
    }

    @Override
    public BookingSummaryResponse getBookingSummary(String bookingId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        List<ScreeningSeat> screeningSeats = screeningSeatRepository.findByBooking(bookingId);
        List<BookingCombo> combo = bookingComboRepository.findByBookingId(bookingId);

        return bookingSummaryMapper.toSummaryResponse(
                booking,
                combo,
                (screeningSeats.stream().map(ScreeningSeat::getSeat).toList())
                        .stream().map(seatMapper::toSeatResponse).toList());
    }
}
