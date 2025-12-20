package com.theatermgnt.theatermgnt.bookingCombo.repository;

import com.theatermgnt.theatermgnt.bookingCombo.entity.BookingCombo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingComboRepository extends JpaRepository<BookingCombo, String> {
    void deleteByBookingId(String bookingId);
    List<BookingCombo> findByBookingId(String bookingId);
}
