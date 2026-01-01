package com.theatermgnt.theatermgnt.booking.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.theatermgnt.theatermgnt.booking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query("""
		SELECT b FROM Booking b
		WHERE b.status IN ('PENDING', 'CONFIRM')
		AND b.expiredAt < :now
		""")
    List<Booking> findExpiredPendingBookings(Instant now);
}
