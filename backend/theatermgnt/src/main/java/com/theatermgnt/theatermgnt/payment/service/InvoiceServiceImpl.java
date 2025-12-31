package com.theatermgnt.theatermgnt.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.theatermgnt.theatermgnt.booking.entity.Booking;
import com.theatermgnt.theatermgnt.booking.repository.BookingRepository;
import com.theatermgnt.theatermgnt.booking.service.BookingService;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
import com.theatermgnt.theatermgnt.payment.dto.request.CreateInvoiceRequest;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceDetailResponse;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceResponse;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceStatisticsResponse;
import com.theatermgnt.theatermgnt.payment.entity.Invoice;
import com.theatermgnt.theatermgnt.payment.entity.InvoiceStatus;
import com.theatermgnt.theatermgnt.payment.mapper.InvoiceMapper;
import com.theatermgnt.theatermgnt.payment.repository.InvoiceRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final InvoiceMapper invoiceMapper;

    private static final int INVOICE_EXPIRY_DAYS = 7;

    // Constructor with @Lazy for BookingService to break circular dependency
    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            BookingRepository bookingRepository,
            @Lazy BookingService bookingService,
            InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        log.info("Creating invoice for booking: {}", request.getBookingId());

        // Check if booking exists
        Booking booking = bookingRepository
                .findById(UUID.fromString(request.getBookingId()))
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));

        // Check if invoice already exists for this booking
        if (invoiceRepository.findByBookingId(request.getBookingId()).isPresent()) {
            throw new AppException(ErrorCode.BOOKING_NOT_EXISTED); // or custom error
        }

        // Create invoice
        Invoice invoice = Invoice.builder()
                .bookingId(request.getBookingId())
                .totalAmount(booking.getTotalAmount())
                .status(InvoiceStatus.PENDING)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully: {}", savedInvoice.getId());

        return invoiceMapper.toResponse(savedInvoice);
    }

    @Override
    public InvoiceResponse getInvoice(String invoiceId) {
        log.info("Fetching invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository
                .findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));

        return invoiceMapper.toResponse(invoice);
    }

    @Override
    public InvoiceResponse getInvoiceByBookingId(String bookingId) {
        log.info("Fetching invoice by booking: {}", bookingId);

        Invoice invoice = invoiceRepository
                .findByBookingId(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));

        return invoiceMapper.toResponse(invoice);
    }

    @Override
    public InvoiceResponse updateInvoiceStatus(String invoiceId, InvoiceStatus status) {
        log.info("Updating invoice {} status to: {}", invoiceId, status);

        Invoice invoice = invoiceRepository
                .findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));

        invoice.setStatus(status);

        if (status == InvoiceStatus.PAID) {
            invoice.setPaidAt(LocalDateTime.now());
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice status updated successfully");

        return invoiceMapper.toResponse(updatedInvoice);
    }

    @Override
    public InvoiceResponse markAsPaid(String invoiceId) {
        return updateInvoiceStatus(invoiceId, InvoiceStatus.PAID);
    }

    @Override
    public InvoiceResponse markAsFailed(String invoiceId) {
        return updateInvoiceStatus(invoiceId, InvoiceStatus.FAILED);
    }

    @Override
    public Page<InvoiceResponse> getAllInvoices(int page, int size) {
        log.info("Fetching all invoices - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoices = invoiceRepository.findAllByOrderByCreatedAtDesc(pageable);
        return invoices.map(invoiceMapper::toResponse);
    }

    @Override
    public Page<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status, int page, int size) {
        log.info("Fetching invoices by status: {} - page: {}, size: {}", status, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoices = invoiceRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return invoices.map(invoiceMapper::toResponse);
    }

    @Override
    public Page<InvoiceResponse> getInvoicesByDateRange(
            LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        log.info("Fetching invoices by date range: {} to {} - page: {}, size: {}", startDate, endDate, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoices =
                invoiceRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate, pageable);
        return invoices.map(invoiceMapper::toResponse);
    }

    @Override
    public Page<InvoiceResponse> searchInvoices(String search, int page, int size) {
        log.info("Searching invoices: {} - page: {}, size: {}", search, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoices = invoiceRepository.searchInvoices(search, pageable);
        return invoices.map(invoiceMapper::toResponse);
    }

    @Override
    public Page<InvoiceResponse> searchInvoicesByStatus(String search, InvoiceStatus status, int page, int size) {
        log.info("Searching invoices by status: {} search: {} - page: {}, size: {}", status, search, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Invoice> invoices = invoiceRepository.searchInvoicesByStatus(search, status, pageable);
        return invoices.map(invoiceMapper::toResponse);
    }

    @Override
    public InvoiceDetailResponse getInvoiceDetail(String invoiceId) {
        log.info("Fetching invoice detail: {}", invoiceId);

        Invoice invoice = invoiceRepository
                .findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));

        log.info("Invoice found: {} with bookingId: {}", invoice.getId(), invoice.getBookingId());

        try {
            UUID bookingUuid = UUID.fromString(invoice.getBookingId());
            log.info("Fetching booking summary for UUID: {}", bookingUuid);
            
            InvoiceDetailResponse response = InvoiceDetailResponse.builder()
                    .id(invoice.getId())
                    .bookingId(invoice.getBookingId())
                    .totalAmount(invoice.getTotalAmount())
                    .status(invoice.getStatus().name())
                    .createdAt(invoice.getCreatedAt())
                    .paidAt(invoice.getPaidAt())
                    .bookingDetails(bookingService.getBookingSummary(bookingUuid))
                    .build();

            log.info("Successfully built invoice detail response for invoice: {}", invoiceId);
            return response;
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for bookingId: {}", invoice.getBookingId(), e);
            throw new AppException(ErrorCode.INVALID_KEY);
        } catch (Exception e) {
            log.error("Error fetching booking details for invoice: {} with bookingId: {}", 
                      invoiceId, invoice.getBookingId(), e);
            throw new AppException(ErrorCode.BOOKING_NOT_EXISTED);
        }
    }

    @Override
    public InvoiceStatisticsResponse getStatistics() {
        log.info("Calculating invoice statistics");

        Long totalInvoices = invoiceRepository.count();
        Long pendingInvoices = invoiceRepository.countByStatus(InvoiceStatus.PENDING);
        Long paidInvoices = invoiceRepository.countByStatus(InvoiceStatus.PAID);
        Long failedInvoices = invoiceRepository.countByStatus(InvoiceStatus.FAILED);
        Long refundedInvoices = invoiceRepository.countByStatus(InvoiceStatus.REFUNDED);

        Double totalRevenue = invoiceRepository.sumTotalAmountByStatus(InvoiceStatus.PAID);
        Double pendingAmount = invoiceRepository.sumTotalAmountByStatus(InvoiceStatus.PENDING);
        Double refundedAmount = invoiceRepository.sumTotalAmountByStatus(InvoiceStatus.REFUNDED);

        return InvoiceStatisticsResponse.builder()
                .totalInvoices(totalInvoices)
                .pendingInvoices(pendingInvoices)
                .paidInvoices(paidInvoices)
                .failedInvoices(failedInvoices)
                .refundedInvoices(refundedInvoices)
                .totalRevenue(BigDecimal.valueOf(totalRevenue != null ? totalRevenue : 0))
                .pendingAmount(BigDecimal.valueOf(pendingAmount != null ? pendingAmount : 0))
                .refundedAmount(BigDecimal.valueOf(refundedAmount != null ? refundedAmount : 0))
                .build();
    }
}
