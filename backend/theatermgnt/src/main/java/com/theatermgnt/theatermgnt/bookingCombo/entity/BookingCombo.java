package com.theatermgnt.theatermgnt.bookingCombo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking_combos")
public class BookingCombo {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String bookingId;

    @Column(nullable = false)
    String comboId;

    @Column(nullable = false)
    String comboName;

    Integer quantity;

    @Column(nullable = false)
    BigDecimal unitPrice;

    @Column(nullable = false)
    BigDecimal subtotal;
}
