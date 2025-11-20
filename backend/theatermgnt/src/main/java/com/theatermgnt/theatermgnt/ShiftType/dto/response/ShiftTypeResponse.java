package com.theatermgnt.theatermgnt.ShiftType.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftTypeResponse {

    String id;
    String cinemaId;

    String name;
    LocalTime startTime;
    LocalTime endTime;
}
