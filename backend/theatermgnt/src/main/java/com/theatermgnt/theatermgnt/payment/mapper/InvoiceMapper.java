package com.theatermgnt.theatermgnt.payment.mapper;

import com.theatermgnt.theatermgnt.payment.dto.response.InvoiceResponse;
import com.theatermgnt.theatermgnt.payment.entity.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {
    
    public InvoiceResponse toResponse(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .bookingId(invoice.getBookingId())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus().name())
                .createdAt(invoice.getCreatedAt())
                .paidAt(invoice.getPaidAt())
                .dueDate(invoice.getDueDate())
                .build();
    }
}
