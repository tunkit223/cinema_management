package com.theatermgnt.theatermgnt.payment.service;

import com.theatermgnt.theatermgnt.booking.entity.Booking;
import com.theatermgnt.theatermgnt.booking.repository.BookingRepository;
import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
import com.theatermgnt.theatermgnt.payment.dto.request.CreateInvoiceRequest;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceResponse;
import com.theatermgnt.theatermgnt.payment.entity.Invoice;
import com.theatermgnt.theatermgnt.payment.entity.InvoiceStatus;
import com.theatermgnt.theatermgnt.payment.mapper.InvoiceMapper;
import com.theatermgnt.theatermgnt.payment.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final InvoiceMapper invoiceMapper;
    
    private static final int INVOICE_EXPIRY_DAYS = 7;
    
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
                .dueDate(LocalDateTime.now().plusDays(INVOICE_EXPIRY_DAYS))
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
}
