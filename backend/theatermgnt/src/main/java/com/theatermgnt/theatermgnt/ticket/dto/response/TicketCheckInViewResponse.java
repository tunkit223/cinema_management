package com.theatermgnt.theatermgnt.ticket.dto.response;

import com.theatermgnt.theatermgnt.bookingCombo.dto.response.ComboCheckInResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketCheckInViewResponse {
    TicketResponse ticket;
    List<ComboCheckInResponse> comboCheckIn;
}
