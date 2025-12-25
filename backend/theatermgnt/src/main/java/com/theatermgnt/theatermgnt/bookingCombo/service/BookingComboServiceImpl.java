package com.theatermgnt.theatermgnt.bookingCombo.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.theatermgnt.theatermgnt.booking.dto.response.BookingPricingResponse;
import com.theatermgnt.theatermgnt.booking.entity.Booking;
import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;
import com.theatermgnt.theatermgnt.booking.mapper.BookingPricingMapper;
import com.theatermgnt.theatermgnt.booking.repository.BookingRepository;
import com.theatermgnt.theatermgnt.bookingCombo.dto.request.ComboItemRequest;
import com.theatermgnt.theatermgnt.bookingCombo.dto.request.UpdateBookingCombosRequest;
import com.theatermgnt.theatermgnt.bookingCombo.entity.BookingCombo;
import com.theatermgnt.theatermgnt.bookingCombo.repository.BookingComboRepository;
import com.theatermgnt.theatermgnt.combo.entity.Combo;
import com.theatermgnt.theatermgnt.combo.repository.ComboRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingComboServiceImpl implements BookingComboService {
    private final BookingComboRepository bookingComboRepository;
    private final BookingRepository bookingRepository;
    private final ComboRepository comboRepository;
    private final BookingPricingMapper bookingPricingMapper;

    @Override
    public BookingPricingResponse updateCombos(UUID bookingId, UpdateBookingCombosRequest request) {
        Booking booking = getValidPendingBooking(bookingId);

        BigDecimal oldComboSubtotal = bookingComboRepository.sumSubtotalByBookingId(bookingId.toString());
        booking.setSubtotal(booking.getSubtotal().subtract(oldComboSubtotal));

        // Xóa tất cả các BookingCombo hiện có cho bookingId
        bookingComboRepository.deleteByBookingId(bookingId.toString());
        BigDecimal comboSubtotal = BigDecimal.ZERO;

        // Tạo mới các BookingCombo từ request
        for (ComboItemRequest item : request.getCombos()) {

            Combo combo = comboRepository
                    .findById(item.getComboId())
                    .orElseThrow(() -> new IllegalStateException("Combo not found"));

            BigDecimal unitPrice = combo.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            BookingCombo bc = new BookingCombo(
                    null,
                    bookingId.toString(),
                    item.getComboId(),
                    combo.getName(),
                    item.getQuantity(),
                    unitPrice,
                    subtotal);

            bookingComboRepository.save(bc);
            comboSubtotal = comboSubtotal.add(subtotal);
        }

        // Update booking pricing
        booking.setSubtotal(booking.getSubtotal().add(comboSubtotal));
        booking.setTotalAmount(booking.getSubtotal().subtract(booking.getDiscount()));

        return bookingPricingMapper.toPricingResponse(booking);
    }

    private Booking getValidPendingBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Booking is not editable");
        }

        if (booking.getExpiredAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Booking expired");
        }

        return booking;
    }
}
