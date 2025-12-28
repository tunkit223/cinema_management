package com.theatermgnt.theatermgnt.ticket.controller;

import com.theatermgnt.theatermgnt.common.dto.response.ApiResponse;
import com.theatermgnt.theatermgnt.ticket.dto.response.TicketResponse;
import com.theatermgnt.theatermgnt.ticket.entity.Ticket;
import com.theatermgnt.theatermgnt.ticket.mapper.TicketMapper;
import com.theatermgnt.theatermgnt.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    @PostMapping("/create/{bookingId}")
    public ApiResponse<List<TicketResponse>> createTickets(@PathVariable UUID bookingId) {

        List<Ticket> tickets = ticketService.createTickets(bookingId);

        return ApiResponse.<List<TicketResponse>>builder()
                .result(tickets.stream().map(ticketMapper::toResponse).toList())
                .build();
    }

    @GetMapping("/by-booking/{bookingId}")
    public List<TicketResponse> getTicketsByBooking(@PathVariable UUID bookingId) {

        return ticketService.getTicketsByBooking(bookingId);
    }

    @GetMapping("/{ticketCode}")
    public TicketResponse getTicketByCode(@PathVariable String ticketCode) {

        Ticket ticket = ticketService.getTicketByCode(ticketCode);

        return ticketMapper.toResponse(ticket);
    }

    @GetMapping("/my-tickets/{customerId}")
    public List<TicketResponse> getTicketsByCustomer(@PathVariable String customerId) {
        List<Ticket> tickets = ticketService.getTicketsByCustomerId(customerId);
        return tickets.stream().map(ticketMapper::toResponse).toList();
    }
}
