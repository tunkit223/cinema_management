package com.theatermgnt.theatermgnt.booking.entity;

import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;
import com.theatermgnt.theatermgnt.customer.entity.Customer;
import com.theatermgnt.theatermgnt.screening.entity.Screening;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    Screening screening;

    @Enumerated(EnumType.STRING)
    BookingStatus status;

    BigDecimal subtotal;
    BigDecimal discount;
    BigDecimal totalAmount;

    Instant createdAt;
    Instant expiredAt;

    public void recalculateTotal(BigDecimal comboSubtotal) {
        this.totalAmount =
                this.subtotal
                        .add(comboSubtotal)
                        .subtract(discount != null ? discount : BigDecimal.ZERO);
    }
}
