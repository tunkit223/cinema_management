package com.theatermgnt.theatermgnt.payment.entity;

import com.theatermgnt.theatermgnt.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, unique = true)
    private String txnRef; // Transaction reference (order ID)
    
    @Column(nullable = false)
    private Long amount; // VND amount (in cents, VNPay requires * 100)
    
    private String orderInfo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    
    private String bankCode;
    private String bankTranNo;
    private String cardType;
    private String paymentDate;
    private String vnpayTransactionNo;
    private String responseCode;
    private String transactionStatus;
    
    private String customerId;
    
    @Column(columnDefinition = "TEXT")
    private String rawCallbackData;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
