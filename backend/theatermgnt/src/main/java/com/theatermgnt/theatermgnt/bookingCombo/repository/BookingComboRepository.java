package com.theatermgnt.theatermgnt.bookingCombo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.theatermgnt.theatermgnt.bookingCombo.entity.BookingCombo;

public interface BookingComboRepository extends JpaRepository<BookingCombo, String> {
    void deleteByBookingId(String bookingId);

    List<BookingCombo> findByBookingId(String bookingId);
}
