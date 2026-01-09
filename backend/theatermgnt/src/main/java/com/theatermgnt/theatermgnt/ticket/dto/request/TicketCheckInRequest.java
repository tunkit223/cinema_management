package com.theatermgnt.theatermgnt.ticket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketCheckInRequest {
    @NotNull
    String ticketCode;

    List<ComboUse> comboUseList;
}
