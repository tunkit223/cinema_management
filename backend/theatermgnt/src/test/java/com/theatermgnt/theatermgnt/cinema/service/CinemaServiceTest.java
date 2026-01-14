package com.theatermgnt.theatermgnt.cinema.service;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.theatermgnt.theatermgnt.cinema.dto.request.CinemaCreationRequest;
import com.theatermgnt.theatermgnt.cinema.dto.request.CinemaUpdateRequest;
import com.theatermgnt.theatermgnt.cinema.dto.response.CinemaResponse;
import com.theatermgnt.theatermgnt.cinema.entity.Cinema;
import com.theatermgnt.theatermgnt.cinema.mapper.CinemaMapper;
import com.theatermgnt.theatermgnt.cinema.repository.CinemaRepository;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.constant.PredefinedRole;
import com.theatermgnt.theatermgnt.room.entity.Room;
import com.theatermgnt.theatermgnt.room.repository.RoomRepository;
import com.theatermgnt.theatermgnt.staff.entity.Staff;
import com.theatermgnt.theatermgnt.staff.repository.StaffRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CinemaServiceTest {

    @Mock
    CinemaRepository cinemaRepository;

    @Mock
    StaffRepository staffRepository;

    @Mock
    RoomRepository roomRepository;

    @Mock
    CinemaMapper cinemaMapper;

    @InjectMocks
    CinemaService cinemaService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    // ================= CREATE =================

    @Test
    void createCinema_success_withoutManager() {
        CinemaCreationRequest req = mock(CinemaCreationRequest.class);
        when(req.getName()).thenReturn("Cinema A");
        when(req.getManagerId()).thenReturn(null);

        when(cinemaRepository.existsByName("Cinema A")).thenReturn(false);

        Cinema cinema = new Cinema();
        when(cinemaMapper.toCinemas(req)).thenReturn(cinema);

        Cinema saved = new Cinema();
        saved.setId("c1");
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(saved);

        CinemaResponse response = mock(CinemaResponse.class);
        when(cinemaMapper.toCinemaResponse(saved)).thenReturn(response);

        CinemaResponse result = cinemaService.createCinema(req);

        assertSame(response, result);

        ArgumentCaptor<Cinema> captor = ArgumentCaptor.forClass(Cinema.class);
        verify(cinemaRepository).save(captor.capture());
        assertNotNull(captor.getValue().getCreatedAt());
    }

    @Test
    void createCinema_withManager_success() {
        CinemaCreationRequest req = mock(CinemaCreationRequest.class);
        when(req.getName()).thenReturn("Cinema B");
        when(req.getManagerId()).thenReturn("m1");

        when(cinemaRepository.existsByName("Cinema B")).thenReturn(false);

        Staff manager = new Staff();
        manager.setId("m1");
        when(staffRepository.findById("m1")).thenReturn(Optional.of(manager));

        Cinema cinema = new Cinema();
        when(cinemaMapper.toCinemas(req)).thenReturn(cinema);

        when(cinemaRepository.save(any())).thenReturn(cinema);
        when(cinemaMapper.toCinemaResponse(cinema)).thenReturn(mock(CinemaResponse.class));

        cinemaService.createCinema(req);

        assertEquals(manager, cinema.getManager());
    }

    @Test
    void createCinema_nameExisted_throws() {
        CinemaCreationRequest req = mock(CinemaCreationRequest.class);
        when(req.getName()).thenReturn("Cinema X");
        when(cinemaRepository.existsByName("Cinema X")).thenReturn(true);

        assertThrows(AppException.class, () -> cinemaService.createCinema(req));
    }

    @Test
    void createCinema_managerNotFound_throws() {
        CinemaCreationRequest req = mock(CinemaCreationRequest.class);
        when(req.getName()).thenReturn("Cinema Y");
        when(req.getManagerId()).thenReturn("missing");

        when(cinemaRepository.existsByName(any())).thenReturn(false);

        when(cinemaMapper.toCinemas(req)).thenReturn(new Cinema());

        when(staffRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> cinemaService.createCinema(req));
    }

    // ================= READ =================

    @Test
    void getCinemas_mapsAll() {
        Cinema c1 = new Cinema();
        Cinema c2 = new Cinema();
        when(cinemaRepository.findAll()).thenReturn(List.of(c1, c2));
        when(cinemaMapper.toCinemaResponse(any())).thenReturn(mock(CinemaResponse.class));

        List<CinemaResponse> res = cinemaService.getCinemas();
        assertEquals(2, res.size());
    }

    @Test
    void getCinema_exists_returns() {
        Cinema c = new Cinema();
        when(cinemaRepository.findById("c1")).thenReturn(Optional.of(c));
        when(cinemaMapper.toCinemaResponse(c)).thenReturn(mock(CinemaResponse.class));

        assertNotNull(cinemaService.getCinema("c1"));
    }

    @Test
    void getCinema_notFound_throws() {
        when(cinemaRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> cinemaService.getCinema("x"));
    }

    // ================= DELETE =================

    @Test
    void deleteCinema_notFound_throws() {
        when(cinemaRepository.existsById("x")).thenReturn(false);
        assertThrows(AppException.class, () -> cinemaService.deleteCinema("x"));
    }

    @Test
    void deleteCinema_hasRooms_throws() {
        when(cinemaRepository.existsById("c1")).thenReturn(true);
        when(roomRepository.findByCinemaId("c1")).thenReturn(List.of(new Room()));

        assertThrows(AppException.class, () -> cinemaService.deleteCinema("c1"));
    }

    @Test
    void deleteCinema_success() {
        when(cinemaRepository.existsById("c1")).thenReturn(true);
        when(roomRepository.findByCinemaId("c1")).thenReturn(List.of());

        cinemaService.deleteCinema("c1");

        verify(cinemaRepository).deleteById("c1");
    }

    // ================= UPDATE =================

    @Test
    void updateCinema_notFound_throws() {
        when(cinemaRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> cinemaService.updateCinema("x", mock(CinemaUpdateRequest.class)));
    }

    @Test
    void updateCinema_clearManager() {
        Cinema cinema = new Cinema();
        when(cinemaRepository.findById("c1")).thenReturn(Optional.of(cinema));

        CinemaUpdateRequest req = mock(CinemaUpdateRequest.class);
        when(req.getManagerId()).thenReturn("");

        when(cinemaRepository.save(cinema)).thenReturn(cinema);
        when(cinemaMapper.toCinemaResponse(cinema)).thenReturn(mock(CinemaResponse.class));

        cinemaService.updateCinema("c1", req);

        assertNull(cinema.getManager());
    }

    @Test
    void updateCinema_setManager_success() {
        Cinema cinema = new Cinema();
        when(cinemaRepository.findById("c1")).thenReturn(Optional.of(cinema));

        CinemaUpdateRequest req = mock(CinemaUpdateRequest.class);
        when(req.getManagerId()).thenReturn("m1");

        Staff manager = new Staff();
        when(staffRepository.findById("m1")).thenReturn(Optional.of(manager));

        when(cinemaRepository.save(cinema)).thenReturn(cinema);
        when(cinemaMapper.toCinemaResponse(cinema)).thenReturn(mock(CinemaResponse.class));

        cinemaService.updateCinema("c1", req);

        assertEquals(manager, cinema.getManager());
    }

    // ================= SECURITY BRANCH =================

    @Test
    void getCinemasForBufferManagement_admin_seesAll() {
        var auth = new UsernamePasswordAuthenticationToken(
                "admin", "pw", List.of(new SimpleGrantedAuthority("ROLE_" + PredefinedRole.ADMIN_ROLE)));
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(cinemaRepository.findAll()).thenReturn(List.of(new Cinema()));
        when(cinemaMapper.toCinemaResponse(any())).thenReturn(mock(CinemaResponse.class));

        assertEquals(1, cinemaService.getCinemasForBufferManagement().size());
    }

    @Test
    void getCinemasForBufferManagement_manager_success() {
        var auth = new UsernamePasswordAuthenticationToken("acc1", "pw", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Staff staff = new Staff();
        staff.setCinemaId("c1");

        when(staffRepository.findByAccountId("acc1")).thenReturn(Optional.of(staff));
        when(cinemaRepository.findById("c1")).thenReturn(Optional.of(new Cinema()));
        when(cinemaMapper.toCinemaResponse(any())).thenReturn(mock(CinemaResponse.class));

        assertEquals(1, cinemaService.getCinemasForBufferManagement().size());
    }

    @Test
    void getCinemasForBufferManagement_noCinema_throws() {
        var auth = new UsernamePasswordAuthenticationToken("acc", "pw", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Staff staff = new Staff();
        when(staffRepository.findByAccountId("acc")).thenReturn(Optional.of(staff));

        assertThrows(AppException.class, () -> cinemaService.getCinemasForBufferManagement());
    }

    // ================= BUFFER =================

    @Test
    void updateCinemaBuffer_success() {
        Cinema cinema = new Cinema();
        when(cinemaRepository.findById("c1")).thenReturn(Optional.of(cinema));
        when(cinemaRepository.save(cinema)).thenReturn(cinema);
        when(cinemaMapper.toCinemaResponse(cinema)).thenReturn(mock(CinemaResponse.class));

        cinemaService.updateCinemaBuffer("c1", 10);

        assertEquals(10, cinema.getBuffer());
    }
}
