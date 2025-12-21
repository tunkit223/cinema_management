package com.theatermgnt.theatermgnt.booking.service;

import com.theatermgnt.theatermgnt.booking.entity.Booking;
import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;
import com.theatermgnt.theatermgnt.booking.repository.BookingRepository;
import com.theatermgnt.theatermgnt.screeningSeat.repository.ScreeningSeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingExpirationService {
    private final BookingRepository bookingRepository;
    private final ScreeningSeatRepository screeningSeatRepository;

    @Scheduled(fixedDelay = 10_000) // mỗi 10 giây
    @Transactional
    public void expireBookings() {

        Instant now = Instant.now();

        List<Booking> expiredBookings =
                bookingRepository.findExpiredPendingBookings(now);

        for (Booking booking : expiredBookings) {

            // 1. Update booking
            booking.setStatus(BookingStatus.EXPIRED);

            // 2. Release seat
            screeningSeatRepository
                    .releaseSeatsByBooking(booking.getId().toString());
        }

        bookingRepository.saveAll(expiredBookings);
    }
}
