# Payment Schema Refactoring - Summary

## ✅ Đã Hoàn Thành

### 1. **Entities (Java Models)**
- ✅ `PaymentMethod.java` - Lưu phương thức thanh toán (VNPay, Bank Transfer, etc.)
- ✅ `Invoice.java` - Hóa đơn từ booking
- ✅ `InvoiceStatus.java` - Enum status cho Invoice
- ✅ `Payment.java` - Refactored, gọn gàng hơn
- ✅ `PaymentType.java` - Enum cho loại payment (BOOKING, REFUND)

### 2. **Repositories**
- ✅ `PaymentMethodRepository.java` - Truy vấn payment methods
- ✅ `InvoiceRepository.java` - Truy vấn invoices
- ✅ `PaymentRepository.java` - Refactored để sử dụng invoiceId

### 3. **DTOs**
- ✅ `CreatePaymentRequest.java` - Request tạo payment
- ✅ `CreateInvoiceRequest.java` - Request tạo invoice
- ✅ `InvoiceResponse.java` - Response invoice
- ✅ `PaymentDetailsResponse.java` - Response payment

### 4. **Mappers**
- ✅ `InvoiceMapper.java` - Map Invoice entity → InvoiceResponse
- ✅ `PaymentMapper.java` - Map Payment entity → PaymentDetailsResponse

### 5. **Database Migration**
- ✅ `V2025.12.26__Add_Invoice_and_Refactor_Payment.sql`
  - Tạo `payment_methods` table
  - Tạo `invoices` table
  - Refactor `payments` table
  - Insert default payment methods
  - Create indexes

---

## 📋 Schema Mới

### paymentMethods
```
id (PK, UUID)
name (UNIQUE, VARCHAR)
description (TEXT)
is_active (BOOLEAN)
createdAt (TIMESTAMP)
```

### invoices
```
id (PK, UUID)
bookingId (FK, UNIQUE)
totalAmount (DECIMAL)
status (ENUM: PENDING, PAID, FAILED, REFUNDED)
dueDate (TIMESTAMP, nullable)
createdAt (TIMESTAMP)
paidAt (TIMESTAMP, nullable)
```

### payments
```
id (PK, UUID)
invoiceId (FK)
paymentMethodId (FK)
amount (DECIMAL)
paymentType (ENUM: BOOKING, REFUND)
transactionCode (UNIQUE, nullable)
status (ENUM: PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED)
description (TEXT, nullable)
paymentDate (TIMESTAMP, nullable)
createdAt (TIMESTAMP)
```

---

## 🔄 Mối Quan Hệ

```
Booking (1) ←→ (1) Invoice (1) ←→ (*) Payment
                                    ↓
                             PaymentMethod
```

- **1 Booking** có **1 Invoice**
- **1 Invoice** có thể có **nhiều Payments** (support retry, partial payment)
- **1 PaymentMethod** có thể được dùng cho **nhiều Payments**

---

## 🚀 Bước Tiếp Theo

1. **Update PaymentService** - Sử dụng Invoice + Payment mới
2. **Tạo InvoiceService** - Quản lý invoice lifecycle
3. **Update BookingService** - Tạo invoice khi booking confirmed
4. **Update PaymentController** - Endpoints cho payment & invoice
5. **VNPay Integration** - Liên kết với VNPay sử dụng invoice

---

## 📝 Ghi Chú

- Migration file sẽ tự động run khi app start (với Flyway/Liquibase)
- Có default PaymentMethod cho VNPay + Bank Transfer
- Indexes được tạo để tối ưu query performance
- Tất cả ForeignKey có ON DELETE CASCADE để dễ cleanup
