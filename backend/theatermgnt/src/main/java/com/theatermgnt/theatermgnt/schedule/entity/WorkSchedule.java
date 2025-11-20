package com.theatermgnt.theatermgnt.schedule.entity;

import com.theatermgnt.theatermgnt.ShiftType.entity.ShiftType;
import com.theatermgnt.theatermgnt.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(
        name = "work_schedules",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_date_shift",
                columnNames = {"user_id", "work_date", "shift_type_id"}
        )
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkSchedule extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(name = "cinema_id", nullable = false)
    String cinemaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_type_id", nullable = false)
    ShiftType shiftType;

    @Column(name = "work_date", nullable = false)
    LocalDate workDate;
}
