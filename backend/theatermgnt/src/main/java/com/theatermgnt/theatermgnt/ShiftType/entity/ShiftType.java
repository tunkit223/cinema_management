package com.theatermgnt.theatermgnt.ShiftType.entity;

import com.theatermgnt.theatermgnt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Entity
@Table(name = "shift_types")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftType extends BaseEntity {

    @Column(name = "cinema_id", nullable = false, length = 36)
    String cinemaId;

    @Column(nullable = false, length = 100)
    String name;

    @Column(name = "start_time", nullable = false)
    LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    LocalTime endTime;
}
