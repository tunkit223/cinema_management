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
import com.theatermgnt.theatermgnt.revenue.dto.request.DailyRevenueCreateRequest;
import com.theatermgnt.theatermgnt.revenue.dto.response.DailyRevenueResponse;
import com.theatermgnt.theatermgnt.revenue.entity.DailyRevenueSummary;
import com.theatermgnt.theatermgnt.revenue.repository.DailyRevenueSummaryRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DailyRevenueServiceTest {

    @Mock
    DailyRevenueSummaryRepository dailyRevenueSummaryRepository;

    @InjectMocks
    DailyRevenueService dailyRevenueService;

    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2025, 1, 15);
    }

    // ================= CREATE =================

    @Test
    void create_success() {
        DailyRevenueCreateRequest req = new DailyRevenueCreateRequest();
        req.setCinemaId("cinema1");
        req.setReportDate(testDate);
        req.setTicketRevenue(BigDecimal.valueOf(5000000));
        req.setComboRevenue(BigDecimal.valueOf(1000000));
        req.setNetRevenue(BigDecimal.valueOf(6000000));
        req.setTotalTransactions(150);

        DailyRevenueSummary saved = new DailyRevenueSummary();
        saved.setId("rev1");
        saved.setCinemaId("cinema1");
        saved.setReportDate(testDate);
        saved.setTicketRevenue(BigDecimal.valueOf(5000000));
        saved.setComboRevenue(BigDecimal.valueOf(1000000));
        saved.setNetRevenue(BigDecimal.valueOf(6000000));
        saved.setTotalTransactions(150);

        when(dailyRevenueSummaryRepository.save(any(DailyRevenueSummary.class))).thenReturn(saved);

        DailyRevenueResponse result = dailyRevenueService.create(req);

        assertNotNull(result);
        assertEquals("cinema1", result.getCinemaId());
        assertEquals(testDate, result.getReportDate());
        assertEquals(BigDecimal.valueOf(5000000), result.getTicketRevenue());
        assertEquals(BigDecimal.valueOf(1000000), result.getComboRevenue());
        assertEquals(BigDecimal.valueOf(6000000), result.getNetRevenue());
        assertEquals(150, result.getTotalTransactions());

        verify(dailyRevenueSummaryRepository).save(any(DailyRevenueSummary.class));
    }

    @Test
    void create_nullDate_throws() {
        DailyRevenueCreateRequest req = new DailyRevenueCreateRequest();
        req.setCinemaId("cinema1");
        req.setReportDate(null);

        assertThrows(IllegalArgumentException.class, () -> dailyRevenueService.create(req));
    }

    @Test
    void create_nullCinemaId_success() {
        DailyRevenueCreateRequest req = new DailyRevenueCreateRequest();
        req.setCinemaId(null);
        req.setReportDate(testDate);
        req.setTicketRevenue(BigDecimal.valueOf(5000000));
        req.setComboRevenue(BigDecimal.valueOf(1000000));
        req.setNetRevenue(BigDecimal.valueOf(6000000));
        req.setTotalTransactions(150);

        DailyRevenueSummary saved = new DailyRevenueSummary();
        saved.setId("rev1");
        saved.setReportDate(testDate);

        when(dailyRevenueSummaryRepository.save(any(DailyRevenueSummary.class))).thenReturn(saved);

        DailyRevenueResponse result = dailyRevenueService.create(req);

        assertNotNull(result);
        verify(dailyRevenueSummaryRepository).save(any(DailyRevenueSummary.class));
    }

    // ================= FIND =================

    @Test
    void find_success() {
        DailyRevenueSummary rev1 = new DailyRevenueSummary();
        rev1.setId("rev1");
        rev1.setCinemaId("cinema1");
        rev1.setReportDate(testDate);

        DailyRevenueSummary rev2 = new DailyRevenueSummary();
        rev2.setId("rev2");
        rev2.setCinemaId("cinema1");
        rev2.setReportDate(testDate.plusDays(1));

        when(dailyRevenueSummaryRepository.findFiltered("cinema1", testDate, testDate.plusDays(1)))
                .thenReturn(List.of(rev1, rev2));

        List<DailyRevenueResponse> result = dailyRevenueService.find("cinema1", testDate, testDate.plusDays(1));

        assertEquals(2, result.size());
        verify(dailyRevenueSummaryRepository).findFiltered("cinema1", testDate, testDate.plusDays(1));
    }

    @Test
    void find_empty() {
        when(dailyRevenueSummaryRepository.findFiltered("cinema1", testDate, testDate.plusDays(1)))
                .thenReturn(List.of());

        List<DailyRevenueResponse> result = dailyRevenueService.find("cinema1", testDate, testDate.plusDays(1));

        assertEquals(0, result.size());
    }

    @Test
    void find_invalidDateRange_throws() {
        LocalDate startDate = testDate.plusDays(5);
        LocalDate endDate = testDate;

        assertThrows(AppException.class, () -> dailyRevenueService.find("cinema1", startDate, endDate));
    }

    @Test
    void find_withNullDates() {
        DailyRevenueSummary rev = new DailyRevenueSummary();
        when(dailyRevenueSummaryRepository.findFiltered("cinema1", null, null)).thenReturn(List.of(rev));

        List<DailyRevenueResponse> result = dailyRevenueService.find("cinema1", null, null);

        assertEquals(1, result.size());
    }

    @Test
    void find_onlyStartDate_valid() {
        DailyRevenueSummary rev = new DailyRevenueSummary();
        when(dailyRevenueSummaryRepository.findFiltered("cinema1", testDate, null)).thenReturn(List.of(rev));

        List<DailyRevenueResponse> result = dailyRevenueService.find("cinema1", testDate, null);

        assertEquals(1, result.size());
    }
}
