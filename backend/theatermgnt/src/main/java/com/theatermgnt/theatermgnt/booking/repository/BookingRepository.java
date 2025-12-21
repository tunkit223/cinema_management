package com.theatermgnt.theatermgnt.booking.repository;

import com.theatermgnt.theatermgnt.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    @Query("""
        SELECT b FROM Booking b
        WHERE b.status = 'PENDING'
          AND b.expiredAt < :now
        """)
    List<Booking> findExpiredPendingBookings(Instant now);
}

