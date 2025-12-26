package com.theatermgnt.theatermgnt.payment.repository;

import com.theatermgnt.theatermgnt.payment.entity.Payment;
import com.theatermgnt.theatermgnt.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByTransactionCode(String transactionCode);
    List<Payment> findByInvoiceId(String invoiceId);
    List<Payment> findByStatus(PaymentStatus status);
}
