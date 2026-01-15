package com.theatermgnt.theatermgnt.revenue.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.revenue.dto.request.MovieRevenueCreateRequest;
import com.theatermgnt.theatermgnt.revenue.dto.response.MovieRevenueResponse;
import com.theatermgnt.theatermgnt.revenue.entity.MovieRevenue;
import com.theatermgnt.theatermgnt.revenue.repository.MovieRevenueRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MovieRevenueServiceTest {

    @Mock
    MovieRevenueRepository movieRevenueRepository;

    @InjectMocks
    MovieRevenueService movieRevenueService;

    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2025, 1, 15);
    }

    // ================= CREATE =================

    @Test
    void create_success() {
        MovieRevenueCreateRequest req = new MovieRevenueCreateRequest();
        req.setMovieId("movie1");
        req.setCinemaId("cinema1");
        req.setReportDate(testDate);
        req.setTotalTicketsSold(250);
        req.setTotalRevenue(BigDecimal.valueOf(5000000));

        MovieRevenue saved = new MovieRevenue();
        saved.setId("mrev1");
        saved.setMovieId("movie1");
        saved.setCinemaId("cinema1");
        saved.setReportDate(testDate);
        saved.setTotalTicketsSold(250);
        saved.setTotalRevenue(BigDecimal.valueOf(5000000));

        when(movieRevenueRepository.save(any(MovieRevenue.class))).thenReturn(saved);

        MovieRevenueResponse result = movieRevenueService.create(req);

        assertNotNull(result);
        assertEquals("movie1", result.getMovieId());
        assertEquals("cinema1", result.getCinemaId());
        assertEquals(testDate, result.getReportDate());
        assertEquals(250, result.getTotalTicketsSold());
        assertEquals(BigDecimal.valueOf(5000000), result.getTotalRevenue());

        verify(movieRevenueRepository).save(any(MovieRevenue.class));
    }

    @Test
    void create_nullDate_throws() {
        MovieRevenueCreateRequest req = new MovieRevenueCreateRequest();
        req.setMovieId("movie1");
        req.setCinemaId("cinema1");
        req.setReportDate(null);
        req.setTotalTicketsSold(250);
        req.setTotalRevenue(BigDecimal.valueOf(5000000));

        assertThrows(IllegalArgumentException.class, () -> movieRevenueService.create(req));
    }

    @Test
    void create_zeroTickets() {
        MovieRevenueCreateRequest req = new MovieRevenueCreateRequest();
        req.setMovieId("movie1");
        req.setCinemaId("cinema1");
        req.setReportDate(testDate);
        req.setTotalTicketsSold(0);
        req.setTotalRevenue(BigDecimal.ZERO);

        MovieRevenue saved = new MovieRevenue();
        saved.setId("mrev1");
        saved.setMovieId("movie1");
        saved.setTotalTicketsSold(0);
        saved.setTotalRevenue(BigDecimal.ZERO);

        when(movieRevenueRepository.save(any(MovieRevenue.class))).thenReturn(saved);

        MovieRevenueResponse result = movieRevenueService.create(req);

        assertNotNull(result);
        assertEquals(0, result.getTotalTicketsSold());

        verify(movieRevenueRepository).save(any(MovieRevenue.class));
    }

    // ================= FIND =================

    @Test
    void find_success() {
        MovieRevenue rev1 = new MovieRevenue();
        rev1.setId("mrev1");
        rev1.setMovieId("movie1");
        rev1.setCinemaId("cinema1");

        MovieRevenue rev2 = new MovieRevenue();
        rev2.setId("mrev2");
        rev2.setMovieId("movie1");
        rev2.setCinemaId("cinema1");

        when(movieRevenueRepository.findFiltered("cinema1", "movie1", testDate, testDate.plusDays(7)))
                .thenReturn(List.of(rev1, rev2));

        List<MovieRevenueResponse> result =
                movieRevenueService.find("cinema1", "movie1", testDate, testDate.plusDays(7));

        assertEquals(2, result.size());
        verify(movieRevenueRepository).findFiltered("cinema1", "movie1", testDate, testDate.plusDays(7));
    }

    @Test
    void find_empty() {
        when(movieRevenueRepository.findFiltered("cinema1", "movie1", testDate, testDate.plusDays(7)))
                .thenReturn(List.of());

        List<MovieRevenueResponse> result =
                movieRevenueService.find("cinema1", "movie1", testDate, testDate.plusDays(7));

        assertEquals(0, result.size());
    }

    @Test
    void find_invalidDateRange_throws() {
        LocalDate startDate = testDate.plusDays(10);
        LocalDate endDate = testDate;

        assertThrows(AppException.class, () -> movieRevenueService.find("cinema1", "movie1", startDate, endDate));
    }

    @Test
    void find_withNullDates() {
        MovieRevenue rev = new MovieRevenue();
        when(movieRevenueRepository.findFiltered("cinema1", "movie1", null, null)).thenReturn(List.of(rev));

        List<MovieRevenueResponse> result = movieRevenueService.find("cinema1", "movie1", null, null);

        assertEquals(1, result.size());
    }

    @Test
    void find_multipleMovies() {
        MovieRevenue rev1 = new MovieRevenue();
        rev1.setMovieId("movie1");

        MovieRevenue rev2 = new MovieRevenue();
        rev2.setMovieId("movie2");

        when(movieRevenueRepository.findFiltered("cinema1", null, testDate, testDate.plusDays(1)))
                .thenReturn(List.of(rev1, rev2));

        List<MovieRevenueResponse> result =
                movieRevenueService.find("cinema1", null, testDate, testDate.plusDays(1));

        assertEquals(2, result.size());
    }
}
