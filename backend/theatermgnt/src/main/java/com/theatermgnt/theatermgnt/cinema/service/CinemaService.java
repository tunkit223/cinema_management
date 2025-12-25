package com.theatermgnt.theatermgnt.cinema.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.theatermgnt.theatermgnt.cinema.dto.request.CinemaCreationRequest;
import com.theatermgnt.theatermgnt.cinema.dto.request.CinemaUpdateRequest;
import com.theatermgnt.theatermgnt.cinema.dto.response.CinemaResponse;
import com.theatermgnt.theatermgnt.cinema.entity.Cinema;
import com.theatermgnt.theatermgnt.cinema.mapper.CinemaMapper;
import com.theatermgnt.theatermgnt.cinema.repository.CinemaRepository;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
import com.theatermgnt.theatermgnt.room.entity.Room;
import com.theatermgnt.theatermgnt.room.repository.RoomRepository;
import com.theatermgnt.theatermgnt.staff.entity.Staff;
import com.theatermgnt.theatermgnt.staff.repository.StaffRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CinemaService {

    CinemaRepository cinemaRepository;
    StaffRepository staffRepository;
    CinemaMapper cinemaMapper;
    RoomRepository roomRepository;

    public CinemaResponse createCinema(CinemaCreationRequest request) {

        if (cinemaRepository.existsByName(request.getName())) throw new AppException(ErrorCode.CINEMA_EXISTED);

        Cinema cinema = cinemaMapper.toCinemas(request);
        cinema.setCreatedAt(LocalDateTime.now());
        if (request.getManagerId() != null && !request.getManagerId().isEmpty()) {
            Staff manager = staffRepository
                    .findById(request.getManagerId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            cinema.setManager(manager);
        }

        return cinemaMapper.toCinemaResponse(cinemaRepository.save(cinema));
    }

    public List<CinemaResponse> getCinemas() {
        return cinemaRepository.findAll().stream()
                .map(cinemaMapper::toCinemaResponse)
                .toList();
    }

    public CinemaResponse getCinema(String cinemaId) {
        return cinemaMapper.toCinemaResponse(
                cinemaRepository.findById(cinemaId).orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_EXISTED)));
    }

    public void deleteCinema(String cinemaId) {
        if (!cinemaRepository.existsById(cinemaId)) {
            throw new AppException(ErrorCode.CINEMA_NOT_EXISTED);
        }
        List<Room> rooms = roomRepository.findByCinemaId(cinemaId);
        if (!rooms.isEmpty()) {
            throw new AppException(ErrorCode.CINEMA_HAS_ROOMS);
        }
        cinemaRepository.deleteById(cinemaId);
    }

    public CinemaResponse updateCinema(String cinemaId, CinemaUpdateRequest request) {
        Cinema cinema =
                cinemaRepository.findById(cinemaId).orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_EXISTED));

        cinemaMapper.updateCinema(cinema, request);

        String managerId = request.getManagerId();
        if (managerId != null) {
            if (managerId.isEmpty()) {
                cinema.setManager(null);
            } else {
                Staff manager = staffRepository
                        .findById(managerId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                cinema.setManager(manager);
            }
        }
        return cinemaMapper.toCinemaResponse(cinemaRepository.save(cinema));
    }
}
