package com.theatermgnt.theatermgnt.payment.service;

import com.theatermgnt.theatermgnt.payment.dto.request.CreateInvoiceRequest;
import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceResponse;
import com.theatermgnt.theatermgnt.payment.entity.InvoiceStatus;

public interface InvoiceService {
    InvoiceResponse createInvoice(CreateInvoiceRequest request);
    
    InvoiceResponse getInvoice(String invoiceId);
    
    InvoiceResponse getInvoiceByBookingId(String bookingId);
    
    InvoiceResponse updateInvoiceStatus(String invoiceId, InvoiceStatus status);
    
    InvoiceResponse markAsPaid(String invoiceId);
    
    InvoiceResponse markAsFailed(String invoiceId);
}
