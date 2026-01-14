package com.theatermgnt.theatermgnt.revenue.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.theatermgnt.theatermgnt.cinema.service.CinemaService;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.revenue.dto.request.RevenueReportCreateRequest;
import com.theatermgnt.theatermgnt.revenue.dto.request.RevenueReportGenerateRequest;
import com.theatermgnt.theatermgnt.revenue.dto.response.RevenueReportResponse;
import com.theatermgnt.theatermgnt.revenue.entity.DailyRevenueSummary;
import com.theatermgnt.theatermgnt.revenue.entity.RevenueReport;
import com.theatermgnt.theatermgnt.revenue.enums.ReportType;
import com.theatermgnt.theatermgnt.revenue.repository.DailyRevenueSummaryRepository;
import com.theatermgnt.theatermgnt.revenue.repository.RevenueReportRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RevenueReportServiceTest {

    @Mock
    RevenueReportRepository revenueReportRepository;

    @Mock
    DailyRevenueSummaryRepository dailyRevenueSummaryRepository;

    @Mock
    CinemaService cinemaService;

    @InjectMocks
    RevenueReportService revenueReportService;

    private LocalDate testStartDate;
    private LocalDate testEndDate;

    @BeforeEach
    void setUp() {
        testStartDate = LocalDate.of(2025, 1, 1);
        testEndDate = LocalDate.of(2025, 1, 31);
    }

    // ================= CREATE =================

    @Test
    void create_success() {
        RevenueReportCreateRequest req = new RevenueReportCreateRequest();
        req.setCinemaId("cinema1");
        req.setReportType(ReportType.CUSTOM);
        req.setStartDate(testStartDate);
        req.setEndDate(testEndDate);
        req.setTotalTicketRevenue(BigDecimal.valueOf(150000000));
        req.setTotalComboRevenue(BigDecimal.valueOf(30000000));
        req.setNetRevenue(BigDecimal.valueOf(180000000));

        RevenueReport saved = new RevenueReport();
        saved.setId("report1");
        saved.setCinemaId("cinema1");
        saved.setReportType(ReportType.CUSTOM);
        saved.setStartDate(testStartDate);
        saved.setEndDate(testEndDate);
        saved.setTotalTicketRevenue(BigDecimal.valueOf(150000000));
        saved.setTotalComboRevenue(BigDecimal.valueOf(30000000));
        saved.setNetRevenue(BigDecimal.valueOf(180000000));

        when(revenueReportRepository.save(any(RevenueReport.class))).thenReturn(saved);

        RevenueReportResponse result = revenueReportService.create(req);

        assertNotNull(result);
        assertEquals("cinema1", result.getCinemaId());
        assertEquals(ReportType.CUSTOM, result.getReportType());
        assertEquals(testStartDate, result.getStartDate());
        assertEquals(testEndDate, result.getEndDate());

        verify(revenueReportRepository).save(any(RevenueReport.class));
    }

    @Test
    void create_invalidDateRange_throws() {
        RevenueReportCreateRequest req = new RevenueReportCreateRequest();
        req.setCinemaId("cinema1");
        req.setReportType(ReportType.CUSTOM);
        req.setStartDate(testEndDate);
        req.setEndDate(testStartDate);

        assertThrows(AppException.class, () -> revenueReportService.create(req));
    }

    @Test
    void create_sameDates_success() {
        RevenueReportCreateRequest req = new RevenueReportCreateRequest();
        req.setCinemaId("cinema1");
        req.setReportType(ReportType.CUSTOM);
        req.setStartDate(testStartDate);
        req.setEndDate(testStartDate);
        req.setTotalTicketRevenue(BigDecimal.valueOf(5000000));
        req.setTotalComboRevenue(BigDecimal.valueOf(1000000));
        req.setNetRevenue(BigDecimal.valueOf(6000000));

        RevenueReport saved = new RevenueReport();
        saved.setId("report1");
        when(revenueReportRepository.save(any(RevenueReport.class))).thenReturn(saved);

        RevenueReportResponse result = revenueReportService.create(req);

        assertNotNull(result);
        verify(revenueReportRepository).save(any(RevenueReport.class));
    }

    // ================= FIND =================

    @Test
    void find_success() {
        RevenueReport report1 = new RevenueReport();
        report1.setId("report1");
        report1.setCinemaId("cinema1");

        RevenueReport report2 = new RevenueReport();
        report2.setId("report2");
        report2.setCinemaId("cinema1");

        when(revenueReportRepository.findFiltered("cinema1", ReportType.CUSTOM, testStartDate, testEndDate))
                .thenReturn(List.of(report1, report2));

        List<RevenueReportResponse> result =
                revenueReportService.find("cinema1", ReportType.CUSTOM, testStartDate, testEndDate);

        assertEquals(2, result.size());
        verify(revenueReportRepository).findFiltered("cinema1", ReportType.CUSTOM, testStartDate, testEndDate);
    }

    @Test
    void find_empty() {
        when(revenueReportRepository.findFiltered("cinema1", ReportType.CUSTOM, testStartDate, testEndDate))
                .thenReturn(List.of());

        List<RevenueReportResponse> result =
                revenueReportService.find("cinema1", ReportType.CUSTOM, testStartDate, testEndDate);

        assertEquals(0, result.size());
    }

    @Test
    void find_invalidDateRange_throws() {
        assertThrows(AppException.class,
                () -> revenueReportService.find("cinema1", ReportType.CUSTOM, testEndDate, testStartDate));
    }

    @Test
    void find_withNullDates() {
        RevenueReport report = new RevenueReport();
        when(revenueReportRepository.findFiltered("cinema1", ReportType.MONTHLY, null, null))
                .thenReturn(List.of(report));

        List<RevenueReportResponse> result =
                revenueReportService.find("cinema1", ReportType.MONTHLY, null, null);

        assertEquals(1, result.size());
    }

    // ================= GENERATE =================

    @Test
    void generate_customTypeSuccess() {
        RevenueReportGenerateRequest req = new RevenueReportGenerateRequest();
        req.setReportType(ReportType.CUSTOM);
        req.setCinemaId("cinema1");
        req.setStartDate(testStartDate);
        req.setEndDate(testEndDate);

        DailyRevenueSummary daily = new DailyRevenueSummary();
        daily.setTicketRevenue(BigDecimal.valueOf(5000000));
        daily.setComboRevenue(BigDecimal.valueOf(1000000));
        daily.setNetRevenue(BigDecimal.valueOf(6000000));

        when(dailyRevenueSummaryRepository.findFiltered("cinema1", testStartDate, testEndDate))
                .thenReturn(List.of(daily));

        RevenueReport savedReport = new RevenueReport();
        savedReport.setId("report1");
        savedReport.setReportType(ReportType.CUSTOM);
        when(revenueReportRepository.save(any(RevenueReport.class))).thenReturn(savedReport);

        RevenueReportResponse result = revenueReportService.generate(req);

        assertNotNull(result);
        assertEquals("report1", result.getId());
    }

    @Test
    void generate_customTypeInvalidDates_throws() {
        RevenueReportGenerateRequest req = new RevenueReportGenerateRequest();
        req.setReportType(ReportType.CUSTOM);
        req.setCinemaId("cinema1");
        req.setStartDate(testEndDate);
        req.setEndDate(testStartDate);

        assertThrows(AppException.class, () -> revenueReportService.generate(req));
    }

    @Test
    void generate_dailyTypeSuccess() {
        RevenueReportGenerateRequest req = new RevenueReportGenerateRequest();
        req.setReportType(ReportType.DAILY);
        req.setCinemaId("cinema1");
        req.setStartDate(testStartDate);
        req.setEndDate(testStartDate);

        DailyRevenueSummary daily = new DailyRevenueSummary();
        daily.setTicketRevenue(BigDecimal.valueOf(5000000));
        daily.setComboRevenue(BigDecimal.valueOf(1000000));
        daily.setNetRevenue(BigDecimal.valueOf(6000000));

        when(dailyRevenueSummaryRepository.findFiltered("cinema1", testStartDate, testStartDate))
                .thenReturn(List.of(daily));

        RevenueReport savedReport = new RevenueReport();
        savedReport.setId("report1");
        when(revenueReportRepository.save(any(RevenueReport.class))).thenReturn(savedReport);

        RevenueReportResponse result = revenueReportService.generate(req);

        assertNotNull(result);
    }

    @Test
    void generate_monthlyTypeSuccess() {
        RevenueReportGenerateRequest req = new RevenueReportGenerateRequest();
        req.setReportType(ReportType.MONTHLY);
        req.setCinemaId("cinema1");
        req.setStartDate(testStartDate);
        req.setEndDate(testEndDate);

        DailyRevenueSummary daily = new DailyRevenueSummary();
        daily.setTicketRevenue(BigDecimal.valueOf(5000000));
        daily.setComboRevenue(BigDecimal.valueOf(1000000));
        daily.setNetRevenue(BigDecimal.valueOf(6000000));

        when(dailyRevenueSummaryRepository.findFiltered("cinema1", testStartDate, testEndDate))
                .thenReturn(List.of(daily));

        RevenueReport savedReport = new RevenueReport();
        savedReport.setId("report1");
        when(revenueReportRepository.save(any(RevenueReport.class))).thenReturn(savedReport);

        RevenueReportResponse result = revenueReportService.generate(req);

        assertNotNull(result);
    }

    @Test
    void generate_forAllCinemas() {
        RevenueReportGenerateRequest req = new RevenueReportGenerateRequest();
        req.setReportType(ReportType.CUSTOM);
        req.setCinemaId(null);
        req.setStartDate(testStartDate);
        req.setEndDate(testEndDate);

        DailyRevenueSummary daily1 = new DailyRevenueSummary();
        daily1.setCinemaId("cinema1");
        daily1.setTicketRevenue(BigDecimal.valueOf(5000000));

        DailyRevenueSummary daily2 = new DailyRevenueSummary();
        daily2.setCinemaId("cinema2");
        daily2.setTicketRevenue(BigDecimal.valueOf(3000000));

        when(dailyRevenueSummaryRepository.findFiltered(null, testStartDate, testEndDate))
                .thenReturn(List.of(daily1, daily2));

        RevenueReport savedReport = new RevenueReport();
        savedReport.setId("report1");
        when(revenueReportRepository.save(any(RevenueReport.class))).thenReturn(savedReport);

        RevenueReportResponse result = revenueReportService.generate(req);

        assertNotNull(result);
    }

    @Test
    void generate_noData() {
        RevenueReportGenerateRequest req = new RevenueReportGenerateRequest();
        req.setReportType(ReportType.CUSTOM);
        req.setCinemaId("cinema1");
        req.setStartDate(testStartDate);
        req.setEndDate(testEndDate);

        when(dailyRevenueSummaryRepository.findFiltered("cinema1", testStartDate, testEndDate))
                .thenReturn(List.of());

        RevenueReport savedReport = new RevenueReport();
        savedReport.setId("report1");
        when(revenueReportRepository.save(any(RevenueReport.class))).thenReturn(savedReport);

        RevenueReportResponse result = revenueReportService.generate(req);

        assertNotNull(result);
        verify(revenueReportRepository).save(any(RevenueReport.class));
    }
}
