package com.theatermgnt.theatermgnt.equipment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.equipment.dto.request.EquipmentCreationRequest;
import com.theatermgnt.theatermgnt.equipment.dto.request.EquipmentUpdateRequest;
import com.theatermgnt.theatermgnt.equipment.dto.response.EquipmentResponse;
import com.theatermgnt.theatermgnt.equipment.entity.Equipment;
import com.theatermgnt.theatermgnt.equipment.mapper.EquipmentMapper;
import com.theatermgnt.theatermgnt.equipment.repository.EquipmentRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EquipmentServiceTest {

    @Mock
    EquipmentRepository equipmentRepository;

    @Mock
    EquipmentMapper equipmentMapper;

    @InjectMocks
    EquipmentService equipmentService;

    @BeforeEach
    void setUp() {
        // Any setup if needed
    }

    // ================= CREATE =================

    @Test
    void createEquipment_success_withoutSerialNumber() {
        EquipmentCreationRequest req = mock(EquipmentCreationRequest.class);
        when(req.getName()).thenReturn("Projector");
        when(req.getCategoryId()).thenReturn("cat1");
        when(req.getRoomId()).thenReturn("room1");
        when(req.getSerialNumber()).thenReturn(null);
        when(req.getStatus()).thenReturn("ACTIVE");

        Equipment equipment = new Equipment();
        when(equipmentMapper.toEquipment(req)).thenReturn(equipment);

        Equipment saved = new Equipment();
        saved.setId("eq1");
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(saved);

        EquipmentResponse response = mock(EquipmentResponse.class);
        when(equipmentMapper.toEquipmentResponse(saved)).thenReturn(response);

        EquipmentResponse result = equipmentService.createEquipment(req);

        assertSame(response, result);

        ArgumentCaptor<Equipment> captor = ArgumentCaptor.forClass(Equipment.class);
        verify(equipmentRepository).save(captor.capture());
        assertNotNull(captor.getValue().getCreatedAt());
        assertNotNull(captor.getValue().getUpdatedAt());
    }

    @Test
    void createEquipment_success_withSerialNumber() {
        EquipmentCreationRequest req = mock(EquipmentCreationRequest.class);
        when(req.getName()).thenReturn("Speaker");
        when(req.getCategoryId()).thenReturn("cat2");
        when(req.getRoomId()).thenReturn("room2");
        when(req.getSerialNumber()).thenReturn("SN001");
        when(req.getStatus()).thenReturn("ACTIVE");

        when(equipmentRepository.existsBySerialNumber("SN001")).thenReturn(false);

        Equipment equipment = new Equipment();
        when(equipmentMapper.toEquipment(req)).thenReturn(equipment);

        Equipment saved = new Equipment();
        saved.setId("eq2");
        when(equipmentRepository.save(any())).thenReturn(saved);
        when(equipmentMapper.toEquipmentResponse(saved)).thenReturn(mock(EquipmentResponse.class));

        equipmentService.createEquipment(req);

        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    void createEquipment_serialNumberExists_throws() {
        EquipmentCreationRequest req = mock(EquipmentCreationRequest.class);
        when(req.getName()).thenReturn("Projector");
        when(req.getSerialNumber()).thenReturn("SN001");

        when(equipmentRepository.existsBySerialNumber("SN001")).thenReturn(true);

        assertThrows(AppException.class, () -> equipmentService.createEquipment(req));
    }

    // ================= READ =================

    @Test
    void getAllEquipment_mapsAll() {
        Equipment e1 = new Equipment();
        Equipment e2 = new Equipment();
        when(equipmentRepository.findAll()).thenReturn(List.of(e1, e2));
        when(equipmentMapper.toEquipmentResponse(any())).thenReturn(mock(EquipmentResponse.class));

        List<EquipmentResponse> res = equipmentService.getAllEquipment();
        assertEquals(2, res.size());
        verify(equipmentMapper, times(2)).toEquipmentResponse(any());
    }

    @Test
    void getAllEquipment_empty() {
        when(equipmentRepository.findAll()).thenReturn(List.of());

        List<EquipmentResponse> res = equipmentService.getAllEquipment();
        assertEquals(0, res.size());
    }

    @Test
    void getEquipmentByRoom_success() {
        Equipment e1 = new Equipment();
        Equipment e2 = new Equipment();
        when(equipmentRepository.findByRoomId("room1")).thenReturn(List.of(e1, e2));
        when(equipmentMapper.toEquipmentResponse(any())).thenReturn(mock(EquipmentResponse.class));

        List<EquipmentResponse> res = equipmentService.getEquipmentByRoom("room1");
        assertEquals(2, res.size());
    }

    @Test
    void getEquipmentByRoom_empty() {
        when(equipmentRepository.findByRoomId("room_empty")).thenReturn(List.of());

        List<EquipmentResponse> res = equipmentService.getEquipmentByRoom("room_empty");
        assertEquals(0, res.size());
    }

    @Test
    void getEquipmentByCategory_success() {
        Equipment e1 = new Equipment();
        when(equipmentRepository.findByCategoryId("cat1")).thenReturn(List.of(e1));
        when(equipmentMapper.toEquipmentResponse(any())).thenReturn(mock(EquipmentResponse.class));

        List<EquipmentResponse> res = equipmentService.getEquipmentByCategory("cat1");
        assertEquals(1, res.size());
    }

    @Test
    void getEquipmentByStatus_success() {
        Equipment e1 = new Equipment();
        when(equipmentRepository.findByStatus("ACTIVE")).thenReturn(List.of(e1));
        when(equipmentMapper.toEquipmentResponse(any())).thenReturn(mock(EquipmentResponse.class));

        List<EquipmentResponse> res = equipmentService.getEquipmentByStatus("ACTIVE");
        assertEquals(1, res.size());
    }

    @Test
    void getEquipmentByRoomAndStatus_success() {
        Equipment e1 = new Equipment();
        when(equipmentRepository.findByRoomIdAndStatus("room1", "ACTIVE")).thenReturn(List.of(e1));
        when(equipmentMapper.toEquipmentResponse(any())).thenReturn(mock(EquipmentResponse.class));

        List<EquipmentResponse> res = equipmentService.getEquipmentByRoomAndStatus("room1", "ACTIVE");
        assertEquals(1, res.size());
    }

    @Test
    void getEquipment_exists_returns() {
        Equipment e = new Equipment();
        when(equipmentRepository.findById("eq1")).thenReturn(Optional.of(e));
        when(equipmentMapper.toEquipmentResponse(e)).thenReturn(mock(EquipmentResponse.class));

        assertNotNull(equipmentService.getEquipment("eq1"));
    }

    @Test
    void getEquipment_notFound_throws() {
        when(equipmentRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> equipmentService.getEquipment("x"));
    }

    // ================= UPDATE =================

    @Test
    void updateEquipment_notFound_throws() {
        when(equipmentRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(
                AppException.class, () -> equipmentService.updateEquipment("x", mock(EquipmentUpdateRequest.class)));
    }

    @Test
    void updateEquipment_success() {
        Equipment equipment = new Equipment();
        equipment.setSerialNumber("SN001");
        when(equipmentRepository.findById("eq1")).thenReturn(Optional.of(equipment));

        EquipmentUpdateRequest req = mock(EquipmentUpdateRequest.class);
        when(req.getSerialNumber()).thenReturn("SN002");

        when(equipmentRepository.existsBySerialNumber("SN002")).thenReturn(false);

        when(equipmentRepository.save(equipment)).thenReturn(equipment);
        when(equipmentMapper.toEquipmentResponse(equipment)).thenReturn(mock(EquipmentResponse.class));

        equipmentService.updateEquipment("eq1", req);

        verify(equipmentMapper).updateEquipment(equipment, req);
        verify(equipmentRepository).save(equipment);
    }

    @Test
    void updateEquipment_serialNumberAlreadyExists_throws() {
        Equipment equipment = new Equipment();
        equipment.setSerialNumber("SN001");
        when(equipmentRepository.findById("eq1")).thenReturn(Optional.of(equipment));

        EquipmentUpdateRequest req = mock(EquipmentUpdateRequest.class);
        when(req.getSerialNumber()).thenReturn("SN999");

        when(equipmentRepository.existsBySerialNumber("SN999")).thenReturn(true);

        assertThrows(AppException.class, () -> equipmentService.updateEquipment("eq1", req));
    }

    @Test
    void updateEquipment_sameSerialnumber_success() {
        Equipment equipment = new Equipment();
        equipment.setSerialNumber("SN001");
        when(equipmentRepository.findById("eq1")).thenReturn(Optional.of(equipment));

        EquipmentUpdateRequest req = mock(EquipmentUpdateRequest.class);
        when(req.getSerialNumber()).thenReturn("SN001");

        when(equipmentRepository.save(equipment)).thenReturn(equipment);
        when(equipmentMapper.toEquipmentResponse(equipment)).thenReturn(mock(EquipmentResponse.class));

        equipmentService.updateEquipment("eq1", req);

        verify(equipmentRepository).save(equipment);
    }

    @Test
    void updateEquipment_nullSerialNumber_success() {
        Equipment equipment = new Equipment();
        when(equipmentRepository.findById("eq1")).thenReturn(Optional.of(equipment));

        EquipmentUpdateRequest req = mock(EquipmentUpdateRequest.class);
        when(req.getSerialNumber()).thenReturn(null);

        when(equipmentRepository.save(equipment)).thenReturn(equipment);
        when(equipmentMapper.toEquipmentResponse(equipment)).thenReturn(mock(EquipmentResponse.class));

        equipmentService.updateEquipment("eq1", req);

        verify(equipmentRepository).save(equipment);
    }

    // ================= DELETE =================

    @Test
    void deleteEquipment_notFound_throws() {
        when(equipmentRepository.existsById("x")).thenReturn(false);
        assertThrows(AppException.class, () -> equipmentService.deleteEquipment("x"));
    }

    @Test
    void deleteEquipment_success() {
        when(equipmentRepository.existsById("eq1")).thenReturn(true);

        equipmentService.deleteEquipment("eq1");

        verify(equipmentRepository).deleteById("eq1");
    }
}
