package com.theatermgnt.theatermgnt.seat.repository;

import com.theatermgnt.theatermgnt.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, String> {
    List<Seat> findByRoomId(String roomId);

    boolean existsByRowChairAndSeatNumberAndRoomId(String rowChair, Integer seatNumber, String roomId);

    long countByRoomId(String roomId);
}
