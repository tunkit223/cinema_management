package com.theatermgnt.theatermgnt.schedule.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateWorkScheduleRequest {
    String shiftTypeId;
    LocalDate workDate;
}
