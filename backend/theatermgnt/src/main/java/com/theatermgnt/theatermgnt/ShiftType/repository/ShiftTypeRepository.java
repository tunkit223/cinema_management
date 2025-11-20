package com.theatermgnt.theatermgnt.ShiftType.repository;

import com.theatermgnt.theatermgnt.ShiftType.entity.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShiftTypeRepository extends JpaRepository<ShiftType, String> {

    List<ShiftType> findByCinemaId(String cinemaId);

    Optional<ShiftType> findByIdAndCinemaId(String id, String cinemaId);

    boolean existsByCinemaIdAndNameIgnoreCase(String cinemaId, String name);
}
