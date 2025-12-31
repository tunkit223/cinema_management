package com.theatermgnt.theatermgnt.priceConfig.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.theatermgnt.theatermgnt.common.entity.BaseEntity;
import com.theatermgnt.theatermgnt.common.enums.DayType;
import com.theatermgnt.theatermgnt.common.enums.TimeSlot;
import com.theatermgnt.theatermgnt.seatType.entity.SeatType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "price_configs",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"seat_type_id", "day_type", "time_slot"})})
@SQLDelete(sql = "UPDATE price_configs SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class PriceConfig extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type")
    DayType dayType;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot")
    TimeSlot timeSlot;

    @Column(precision = 10, scale = 2)
    BigDecimal price;

    // Quan hệ nhiều-1 với SeatType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_type_id", nullable = false)
    SeatType seatType;
}
