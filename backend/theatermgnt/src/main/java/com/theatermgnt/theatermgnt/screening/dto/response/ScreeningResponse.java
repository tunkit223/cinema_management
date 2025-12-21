package com.theatermgnt.theatermgnt.screening.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScreeningResponse {
     String id;
     String movieId;
     String movieName;
     String roomId;
     String roomName;
     String cinemaId;
     String cinemaName;
     LocalDateTime startTime;
     LocalDateTime endTime;
     String status;
}
