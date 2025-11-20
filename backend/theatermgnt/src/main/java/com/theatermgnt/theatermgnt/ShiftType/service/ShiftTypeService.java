package com.theatermgnt.theatermgnt.ShiftType.service;

import com.theatermgnt.theatermgnt.ShiftType.dto.request.CreateShiftTypeRequest;
import com.theatermgnt.theatermgnt.ShiftType.dto.request.UpdateShiftTypeRequest;
import com.theatermgnt.theatermgnt.ShiftType.dto.response.ShiftTypeResponse;

import java.util.List;

public interface ShiftTypeService {

    ShiftTypeResponse create(String cinemaId, CreateShiftTypeRequest request);

    List<ShiftTypeResponse> getAll(String cinemaId);

    ShiftTypeResponse getById(String cinemaId, String shiftId);

    ShiftTypeResponse update(String cinemaId, String shiftId, UpdateShiftTypeRequest request);

    void delete(String cinemaId, String shiftId);
}
