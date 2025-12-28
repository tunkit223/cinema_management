package com.theatermgnt.theatermgnt.ticket.service;

import com.theatermgnt.theatermgnt.ticket.dto.response.TicketCheckInResponse;
import com.theatermgnt.theatermgnt.ticket.dto.response.TicketResponse;
import com.theatermgnt.theatermgnt.ticket.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketService {
    List<TicketResponse> getTicketsByBooking(UUID bookingId);

    TicketCheckInResponse checkInByQr(String qrContent);

    List<Ticket> createTickets(UUID bookingId);

    Ticket getTicketByCode(String ticketCode);

    List<Ticket> getTicketsByCustomerId(String customerId);
}
