// package com.theatermgnt.theatermgnt.ticket.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// class TicketExpireSchedulerTest {
//
//    @Mock
//    private TicketService ticketService;
//
//    @InjectMocks
//    private TicketExpireScheduler ticketExpireScheduler;
//
//    @BeforeEach
//    void setUp() {}
//
//    // ================= EXPIRE TICKETS JOB TESTS =================
//
//    @Test
//    void expireTicketsJob_callsTicketService() {
//        // When
//        ticketExpireScheduler.expireTicketsJob();
//
//        // Then
//        verify(ticketService, times(1)).expireTickets();
//    }
//
//    @Test
//    void expireTicketsJob_executesSuccessfully() {
//        // Given
//        doNothing().when(ticketService).expireTickets();
//
//        // When - Should not throw exception
//        assertDoesNotThrow(() -> ticketExpireScheduler.expireTicketsJob());
//
//        // Then
//        verify(ticketService).expireTickets();
//    }
//
//    @Test
//    void expireTicketsJob_handlesException() {
//        // Given
//        doThrow(new RuntimeException("Service error")).when(ticketService).expireTickets();
//
//        // When & Then
//        assertThrows(RuntimeException.class, () -> ticketExpireScheduler.expireTicketsJob());
//        verify(ticketService).expireTickets();
//    }
//
//    @Test
//    void expireTicketsJob_multipleInvocations() {
//        // When
//        ticketExpireScheduler.expireTicketsJob();
//        ticketExpireScheduler.expireTicketsJob();
//        ticketExpireScheduler.expireTicketsJob();
//
//        // Then
//        verify(ticketService, times(3)).expireTickets();
//    }
//
//    @Test
//    void expireTicketsJob_callsServiceWithoutParameters() {
//        // When
//        ticketExpireScheduler.expireTicketsJob();
//
//        // Then
//        verify(ticketService, times(1)).expireTickets();
//        verifyNoMoreInteractions(ticketService);
//    }
// }
