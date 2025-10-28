package com.theatermgnt.theatermgnt.priceConfig.entity;

import com.theatermgnt.theatermgnt.common.entity.BaseEntity;
import com.theatermgnt.theatermgnt.common.enums.DayType;
import com.theatermgnt.theatermgnt.common.enums.TimeSlot;
import com.theatermgnt.theatermgnt.seatType.entity.SeatType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "priceConfigs")
@SQLDelete(sql = "UPDATE priceConfigs SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class PriceConfig extends BaseEntity {

    @Enumerated(EnumType.STRING)
    DayType dayType;

    @Enumerated(EnumType.STRING)
    TimeSlot timeSlot;

    @Column(precision = 10, scale = 2)
    BigDecimal price;

    // Quan hệ nhiều-1 với SeatType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatTypeId", nullable = false)
    SeatType seatType;
}
