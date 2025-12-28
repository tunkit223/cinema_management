# Booking & Payment Integration - Complete Implementation Guide

## ✅ **Hoàn Thành Tất Cả**

### **1. Backend Services**
- ✅ `InvoiceService` + `InvoiceServiceImpl` - Quản lý hóa đơn
- ✅ `PaymentService` (refactored) - Tạo VNPay payment URL từ invoice
- ✅ `BookingService` + 2 methods mới:
  - `createInvoiceForBooking(bookingId)` - Tạo invoice từ booking
  - `confirmBookingPayment(bookingId)` - Confirm booking sau payment success

### **2. Controllers**
- ✅ `InvoiceController` - Lấy invoice
- ✅ `PaymentController` (refactored) - VNPay endpoints
- ✅ `BookingController` - Thêm endpoint tạo invoice

### **3. Database**
- ✅ Migration file - Tạo tables mới

---

## 🔄 **Complete Flow: Booking → Invoice → Payment**

### **Step 1: Tạo Booking**
```
POST /api/theater-mgnt/bookings
{
  "customerId": "xxx",
  "screeningId": "yyy",
  "screeningSeatIds": ["seat1", "seat2"],
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com"
}

Response:
{
  "id": "bookingId123",
  "subtotal": 500000,
  "expiredAt": "2025-12-26T14:30:00Z"
}
```

**Status:** Booking = PENDING, Seats = LOCKED for 10 minutes

---

### **Step 2: Add Combo & Redeem Points (optional)**
```
POST /api/theater-mgnt/bookings/{bookingId}/redeem-points
{
  "pointsToRedeem": 100
}

Response: Updated booking summary with discount
```

---

### **Step 3: Tạo Invoice**
```
POST /api/theater-mgnt/bookings/{bookingId}/create-invoice

Response:
{
  "id": "invoiceId456",
  "bookingId": "bookingId123",
  "totalAmount": 450000,
  "status": "PENDING",
  "createdAt": "2025-12-26T12:30:00Z"
}
```

**Status:** Invoice = PENDING, Amount = Booking totalAmount

---

### **Step 4: Initiate VNPay Payment**
```
POST /api/theater-mgnt/payment/vnpay/{invoiceId}

Response:
{
  "code": "00",
  "message": "Success",
  "paymentUrl": "https://sandbox.vnpayment.vn/paygate/pay?...",
  "id": "paymentId789",
  "transactionCode": "invoiceId456_1735200600000",
  "invoiceId": "invoiceId456",
  "amount": 450000,
  "status": "PENDING"
}
```

**Frontend:** Redirect user đến `paymentUrl` để thanh toán

---

### **Step 5: VNPay Callback**
**VNPay gửi callback về `vnpay-return` endpoint:**

```
GET /api/theater-mgnt/payment/vnpay-return?vnp_Amount=450000000&vnp_TxnRef=invoiceId456_1735200600000&vnp_ResponseCode=00&vnp_SecureHash=...
```

**Backend sẽ:**
1. ✅ Verify signature
2. ✅ Update Payment status = SUCCESS
3. ✅ Update Invoice status = PAID
4. ✅ Mark Invoice.paidAt = now

**Response:**
```json
{
  "code": "00",
  "message": "Payment successful",
  "paymentId": "paymentId789",
  "invoiceId": "invoiceId456",
  "txnRef": "invoiceId456_1735200600000",
  "amount": 450000
}
```

---

### **Step 6: Frontend Confirm Payment**
Frontend xử lý callback:
- Lưu paymentId
- Gọi API confirm booking (call `confirmBookingPayment`)
- Show success screen

---

### **Step 7: Confirm Booking (Backend)**
```
TODO: Add endpoint sau
POST /api/theater-mgnt/bookings/{bookingId}/confirm-payment

Backend sẽ:
1. Set Booking.status = CONFIRMED
2. Release seat locks
3. Send confirmation email
4. Return ticket info
```

---

## 📊 **Database Schema**

### Relationships
```
Booking (PENDING) 
  └─→ Invoice (PENDING → PAID)
        └─→ Payment (PENDING → SUCCESS)
              └─→ PaymentMethod
```

### Tables
```sql
-- Booking (existing)
bookings (id, customer_id, screening_id, status: PENDING/CONFIRM/EXPIRED, ...)

-- New
payment_methods (id, name, description, is_active)
invoices (id, booking_id, total_amount, status: PENDING/PAID/FAILED/REFUNDED, ...)
payments (id, invoice_id, payment_method_id, amount, payment_type: BOOKING/REFUND, 
          transaction_code, status: PENDING/SUCCESS/FAILED, ...)
```

---

## 🔑 **Key Methods**

### **InvoiceService**
```java
createInvoice(CreateInvoiceRequest) → InvoiceResponse
getInvoice(invoiceId) → InvoiceResponse
getInvoiceByBookingId(bookingId) → InvoiceResponse
updateInvoiceStatus(invoiceId, status) → InvoiceResponse
markAsPaid(invoiceId) → InvoiceResponse
markAsFailed(invoiceId) → InvoiceResponse
```

### **PaymentService**
```java
createVNPayPayment(invoiceId, httpRequest) → PaymentDetailsResponse
  // Tạo payment record + VNPay URL
  
handleVNPayCallback(params) → Map<String, Object>
  // Verify signature, update payment + invoice status
  
handleVNPayIPN(params) → Map<String, Object>
  // Handle IPN từ VNPay
```

### **BookingService**
```java
createInvoiceForBooking(bookingId) → InvoiceResponse
  // Lấy booking → Tạo invoice

confirmBookingPayment(bookingId) → void
  // Set Booking.status = CONFIRMED
```

---

## 🛠️ **API Endpoints Summary**

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/bookings` | POST | Tạo booking |
| `/bookings/{id}/summary` | GET | Lấy booking summary |
| `/bookings/{id}/redeem-points` | POST | Redeem loyalty points |
| `/bookings/{id}/create-invoice` | POST | Tạo invoice từ booking |
| `/invoices/{id}` | GET | Lấy invoice |
| `/invoices/booking/{bookingId}` | GET | Lấy invoice by booking |
| `/payment/vnpay/{invoiceId}` | POST | Tạo VNPay payment URL |
| `/payment/vnpay-return` | GET | VNPay return callback |
| `/payment/vnpay-ipn` | GET | VNPay IPN callback |

---

## 📝 **Bước Tiếp Theo**

1. **Database Migration:**
   - Run Flyway migration để tạo tables
   - Insert default payment methods

2. **Frontend Integration:**
   - Update booking flow: Step 3 → Create invoice
   - Step 4 → Call payment API, get paymentUrl
   - Redirect to VNPay
   - Handle callback → Show success

3. **Testing:**
   - Use VNPay sandbox
   - Test full payment flow
   - Test refund flow

4. **Confirm Booking Endpoint:**
   - Add endpoint để confirm booking sau payment
   - Update booking status PENDING → CONFIRMED
   - Release seat locks
   - Send confirmation email

---

## 🚀 **Status Tracking**

- Booking: PENDING → CONFIRM → EXPIRED
- Invoice: PENDING → PAID → FAILED → REFUNDED
- Payment: PENDING → SUCCESS → FAILED → CANCELLED → REFUNDED
- Seats: AVAILABLE → LOCKED (10 min) → BOOKED (sau confirm)
