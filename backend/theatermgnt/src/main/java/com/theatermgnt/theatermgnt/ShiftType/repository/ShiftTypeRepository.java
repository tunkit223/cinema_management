package com.theatermgnt.theatermgnt.ShiftType.repository;

import com.theatermgnt.theatermgnt.ShiftType.entity.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShiftTypeRepository extends JpaRepository<ShiftType, String> {

    List<ShiftType> findByCinemaIdAndDeletedFalse(String cinemaId);

    Optional<ShiftType> findByIdAndCinemaIdAndDeletedFalse(String id, String cinemaId);

    boolean existsByCinemaIdAndNameIgnoreCaseAndDeletedFalse(String cinemaId, String name);
}
