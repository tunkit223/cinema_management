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
import com.theatermgnt.theatermgnt.equipment.dto.request.EquipmentCategoryCreationRequest;
import com.theatermgnt.theatermgnt.equipment.dto.request.EquipmentCategoryUpdateRequest;
import com.theatermgnt.theatermgnt.equipment.dto.response.EquipmentCategoryResponse;
import com.theatermgnt.theatermgnt.equipment.entity.EquipmentCategory;
import com.theatermgnt.theatermgnt.equipment.mapper.EquipmentCategoryMapper;
import com.theatermgnt.theatermgnt.equipment.repository.EquipmentCategoryRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EquipmentCategoryServiceTest {

    @Mock
    EquipmentCategoryRepository equipmentCategoryRepository;

    @Mock
    EquipmentCategoryMapper equipmentCategoryMapper;

    @InjectMocks
    EquipmentCategoryService equipmentCategoryService;

    @BeforeEach
    void setUp() {
        // Any setup if needed
    }

    // ================= CREATE =================

    @Test
    void createCategory_success_basic() {
        EquipmentCategoryCreationRequest req = mock(EquipmentCategoryCreationRequest.class);
        when(req.getName()).thenReturn("Projectors");
        when(req.getDescription()).thenReturn("Video projection equipment");

        when(equipmentCategoryRepository.existsByName("Projectors")).thenReturn(false);

        EquipmentCategory category = new EquipmentCategory();
        when(equipmentCategoryMapper.toEquipmentCategory(req)).thenReturn(category);

        EquipmentCategory saved = new EquipmentCategory();
        saved.setId("cat1");
        when(equipmentCategoryRepository.save(any(EquipmentCategory.class))).thenReturn(saved);

        EquipmentCategoryResponse response = mock(EquipmentCategoryResponse.class);
        when(equipmentCategoryMapper.toEquipmentCategoryResponse(saved)).thenReturn(response);

        EquipmentCategoryResponse result = equipmentCategoryService.createCategory(req);

        assertSame(response, result);

        ArgumentCaptor<EquipmentCategory> captor = ArgumentCaptor.forClass(EquipmentCategory.class);
        verify(equipmentCategoryRepository).save(captor.capture());
        assertNotNull(captor.getValue().getCreatedAt());
        assertNotNull(captor.getValue().getUpdatedAt());
    }

    @Test
    void createCategory_success_withoutDescription() {
        EquipmentCategoryCreationRequest req = mock(EquipmentCategoryCreationRequest.class);
        when(req.getName()).thenReturn("Speakers");
        when(req.getDescription()).thenReturn(null);

        when(equipmentCategoryRepository.existsByName("Speakers")).thenReturn(false);

        EquipmentCategory category = new EquipmentCategory();
        when(equipmentCategoryMapper.toEquipmentCategory(req)).thenReturn(category);

        EquipmentCategory saved = new EquipmentCategory();
        saved.setId("cat2");
        when(equipmentCategoryRepository.save(any())).thenReturn(saved);
        when(equipmentCategoryMapper.toEquipmentCategoryResponse(saved))
                .thenReturn(mock(EquipmentCategoryResponse.class));

        equipmentCategoryService.createCategory(req);

        verify(equipmentCategoryRepository).save(any(EquipmentCategory.class));
    }

    @Test
    void createCategory_nameExists_throws() {
        EquipmentCategoryCreationRequest req = mock(EquipmentCategoryCreationRequest.class);
        when(req.getName()).thenReturn("Lighting");

        when(equipmentCategoryRepository.existsByName("Lighting")).thenReturn(true);

        assertThrows(AppException.class, () -> equipmentCategoryService.createCategory(req));
    }

    // ================= READ =================

    @Test
    void getCategories_mapsAll() {
        EquipmentCategory c1 = new EquipmentCategory();
        EquipmentCategory c2 = new EquipmentCategory();
        EquipmentCategory c3 = new EquipmentCategory();
        when(equipmentCategoryRepository.findAll()).thenReturn(List.of(c1, c2, c3));
        when(equipmentCategoryMapper.toEquipmentCategoryResponse(any()))
                .thenReturn(mock(EquipmentCategoryResponse.class));

        List<EquipmentCategoryResponse> res = equipmentCategoryService.getCategories();

        assertEquals(3, res.size());
        verify(equipmentCategoryMapper, times(3)).toEquipmentCategoryResponse(any());
    }

    @Test
    void getCategories_empty() {
        when(equipmentCategoryRepository.findAll()).thenReturn(List.of());

        List<EquipmentCategoryResponse> res = equipmentCategoryService.getCategories();

        assertEquals(0, res.size());
    }

    @Test
    void getCategory_exists_returns() {
        EquipmentCategory c = new EquipmentCategory();
        when(equipmentCategoryRepository.findById("cat1")).thenReturn(Optional.of(c));
        when(equipmentCategoryMapper.toEquipmentCategoryResponse(c)).thenReturn(mock(EquipmentCategoryResponse.class));

        assertNotNull(equipmentCategoryService.getCategory("cat1"));
    }

    @Test
    void getCategory_notFound_throws() {
        when(equipmentCategoryRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> equipmentCategoryService.getCategory("x"));
    }

    // ================= UPDATE =================

    @Test
    void updateCategory_notFound_throws() {
        when(equipmentCategoryRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(
                AppException.class,
                () -> equipmentCategoryService.updateCategory("x", mock(EquipmentCategoryUpdateRequest.class)));
    }

    @Test
    void updateCategory_success() {
        EquipmentCategory category = new EquipmentCategory();
        when(equipmentCategoryRepository.findById("cat1")).thenReturn(Optional.of(category));

        EquipmentCategoryUpdateRequest req = mock(EquipmentCategoryUpdateRequest.class);
        when(req.getName()).thenReturn("Updated Name");
        when(req.getDescription()).thenReturn("Updated Description");

        when(equipmentCategoryRepository.save(category)).thenReturn(category);
        when(equipmentCategoryMapper.toEquipmentCategoryResponse(category))
                .thenReturn(mock(EquipmentCategoryResponse.class));

        equipmentCategoryService.updateCategory("cat1", req);

        verify(equipmentCategoryMapper).updateEquipmentCategory(category, req);
        verify(equipmentCategoryRepository).save(category);
    }

    @Test
    void updateCategory_partialUpdate() {
        EquipmentCategory category = new EquipmentCategory();
        category.setName("Original Name");
        category.setDescription("Original Description");

        when(equipmentCategoryRepository.findById("cat1")).thenReturn(Optional.of(category));

        EquipmentCategoryUpdateRequest req = mock(EquipmentCategoryUpdateRequest.class);
        when(req.getName()).thenReturn("New Name");
        when(req.getDescription()).thenReturn(null);

        when(equipmentCategoryRepository.save(category)).thenReturn(category);
        when(equipmentCategoryMapper.toEquipmentCategoryResponse(category))
                .thenReturn(mock(EquipmentCategoryResponse.class));

        equipmentCategoryService.updateCategory("cat1", req);

        verify(equipmentCategoryMapper).updateEquipmentCategory(category, req);
        verify(equipmentCategoryRepository).save(category);
    }

    @Test
    void updateCategory_timestampUpdated() {
        EquipmentCategory category = new EquipmentCategory();
        when(equipmentCategoryRepository.findById("cat1")).thenReturn(Optional.of(category));

        EquipmentCategoryUpdateRequest req = mock(EquipmentCategoryUpdateRequest.class);

        when(equipmentCategoryRepository.save(category)).thenReturn(category);
        when(equipmentCategoryMapper.toEquipmentCategoryResponse(category))
                .thenReturn(mock(EquipmentCategoryResponse.class));

        equipmentCategoryService.updateCategory("cat1", req);

        ArgumentCaptor<EquipmentCategory> captor = ArgumentCaptor.forClass(EquipmentCategory.class);
        verify(equipmentCategoryRepository).save(captor.capture());
        assertNotNull(captor.getValue().getUpdatedAt());
    }

    // ================= DELETE =================

    @Test
    void deleteCategory_notFound_throws() {
        when(equipmentCategoryRepository.existsById("x")).thenReturn(false);
        assertThrows(AppException.class, () -> equipmentCategoryService.deleteCategory("x"));
    }

    @Test
    void deleteCategory_success() {
        when(equipmentCategoryRepository.existsById("cat1")).thenReturn(true);

        equipmentCategoryService.deleteCategory("cat1");

        verify(equipmentCategoryRepository).deleteById("cat1");
    }

    @Test
    void deleteCategory_verifyOnlyCalledOnce() {
        when(equipmentCategoryRepository.existsById("cat1")).thenReturn(true);

        equipmentCategoryService.deleteCategory("cat1");

        verify(equipmentCategoryRepository, times(1)).deleteById("cat1");
    }
}
