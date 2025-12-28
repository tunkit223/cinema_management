package com.theatermgnt.theatermgnt.ticket.dto.response;

import com.theatermgnt.theatermgnt.ticket.enums.TicketStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketResponse {
    UUID id;
    String ticketCode;
    String qrContent;
    String seatName;
    BigDecimal price;
    TicketStatus status;
    Instant expiresAt;
}
