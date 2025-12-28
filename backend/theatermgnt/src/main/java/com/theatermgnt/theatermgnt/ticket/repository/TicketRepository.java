package com.theatermgnt.theatermgnt.ticket.repository;

import com.theatermgnt.theatermgnt.ticket.entity.Ticket;
import com.theatermgnt.theatermgnt.ticket.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByTicketCode(String ticketCode);

    List<Ticket> findAllByBookingId(UUID bookingId);

    boolean existsByScreeningSeatId(String screeningSeatId);

    List<Ticket> findAllByStatusAndExpiresAtBefore(
            TicketStatus status,
            Instant time
    );

    boolean existsByTicketCode(String ticketCode);

    List<Ticket> findByBooking_Customer_IdOrderByCreatedAtDesc(String customerId);
}
