package com.theatermgnt.theatermgnt.ticket.dto.response;

import com.theatermgnt.theatermgnt.ticket.enums.TicketStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketCheckInResponse {
    String ticketCode;
    TicketStatus status;
    Instant usedAt;
    String message;
}
