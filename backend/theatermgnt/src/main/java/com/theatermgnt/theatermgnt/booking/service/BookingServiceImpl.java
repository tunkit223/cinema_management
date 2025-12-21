package com.theatermgnt.theatermgnt.booking.service;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final BookingComboRepository bookingComboRepository;
    private final ScreeningSeatRepository screeningSeatRepository;
    private final ScreeningRepository screeningRepository;
    private final CustomerRepository customerRepository;
    private final PriceConfigRepository priceConfigRepository;
    private final BookingMapper bookingMapper;
    private final BookingSummaryMapper bookingSummaryMapper;
    private final SeatMapper seatMapper;

    private static final Duration HOLD_DURATION = Duration.ofMinutes(10);

    @Override
    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        if (request.getScreeningSeatIds().isEmpty()) {
            throw new IllegalArgumentException("No seats selected for booking");
        }

        Instant now = Instant.now();
        Instant expiredAt = now.plus(HOLD_DURATION);

        int lockedCount = screeningSeatRepository.lockSeats(
                request.getScreeningSeatIds(),
                expiredAt
        );

        if (lockedCount != request.getScreeningSeatIds().size()) {
            throw new IllegalStateException("Some seats are not available");
        }

        List<ScreeningSeat> seats =
                screeningSeatRepository.findAllById(request.getScreeningSeatIds());
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Screening screening = screeningRepository.findById(request.getScreeningId())
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

    private BigDecimal calculateSeatSubtotal(List<ScreeningSeat> seats) {
        BigDecimal subTotal = BigDecimal.ZERO;
        for( ScreeningSeat seat : seats) {
            Seat s = seat.getSeat();
            Screening screening = seat.getScreening();
            TimeSlot timeSlot = TimeSlot.from(
                    screening.getStartTime().toLocalTime()
            );
            DayType dayType = DayType.from(
                    screening.getStartTime().toLocalDate()
            );
            PriceConfig priceConfig = priceConfigRepository.getPriceBySeatTypeIdAndDayTypeAndTimeSlot(
                    s.getSeatType().getId(),
                    dayType,
                    timeSlot
            );
            if(priceConfig.getPrice() == null) {
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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        List<ScreeningSeat> screeningSeats =
                screeningSeatRepository.findByBooking(bookingId);
        List<BookingCombo> combo = bookingComboRepository
                .findByBookingId(bookingId);

        return bookingSummaryMapper.toSummaryResponse(
                booking,
                combo,
                (screeningSeats.stream().map(ScreeningSeat::getSeat).toList())
                        .stream().map(seatMapper::toSeatResponse).toList()
        );
    }
}
