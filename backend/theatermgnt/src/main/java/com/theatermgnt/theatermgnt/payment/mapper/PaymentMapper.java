package com.theatermgnt.theatermgnt.payment.mapper;

import com.theatermgnt.theatermgnt.payment.dto.response.PaymentDetailsResponse;
import com.theatermgnt.theatermgnt.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    
    public PaymentDetailsResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        return PaymentDetailsResponse.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoiceId())
                .paymentMethodId(payment.getPaymentMethodId())
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType().name())
                .transactionCode(payment.getTransactionCode())
                .status(payment.getStatus().name())
                .description(payment.getDescription())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
