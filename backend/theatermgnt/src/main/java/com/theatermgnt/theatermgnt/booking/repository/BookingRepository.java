package com.theatermgnt.theatermgnt.booking.repository;

import com.theatermgnt.theatermgnt.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, String> {
}

