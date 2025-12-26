//package com.theatermgnt.theatermgnt.ticket.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//
//@Entity
//@Builder
//@AllArgsConstructor
//@RequiredArgsConstructor
//@Setter
//@Getter
//@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
//@Table(name = "tickets")
//public class Ticket {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    String id;
//
//    @Column(nullable = false)
//    String bookingId;
//
//    @Column(nullable = false)
//    String screeningSeatId;
//
//    BigDecimal price;
//
//    String ticketCode;
//
//    @Builder.Default
//    boolean used = false;
//
//    String qrContent;
//    Instant usedAt;
//    Instant expiredAt;
//}
