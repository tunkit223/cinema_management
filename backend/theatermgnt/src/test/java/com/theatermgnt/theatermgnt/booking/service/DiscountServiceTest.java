// package com.theatermgnt.theatermgnt.booking.service;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.math.BigDecimal;
// import java.time.Instant;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// import com.theatermgnt.theatermgnt.booking.entity.Booking;
// import com.theatermgnt.theatermgnt.booking.enums.BookingStatus;
//
// class DiscountServiceTest {
//
//    private DiscountService discountService;
//    private Booking booking;
//
//    @BeforeEach
//    void setUp() {
//        discountService = new DiscountService();
//
//        // Setup booking with subtotal of 200,000 VND
//        booking = new Booking();
//        booking.setId(UUID.randomUUID());
//        booking.setStatus(BookingStatus.PENDING);
//        booking.setSubtotal(BigDecimal.valueOf(200000));
//        booking.setDiscount(BigDecimal.ZERO);
//        booking.setTotalAmount(BigDecimal.valueOf(200000));
//        booking.setCreatedAt(Instant.now());
//        booking.setExpiredAt(Instant.now().plusSeconds(600));
//    }
//
//    // ================= APPLY DISCOUNTS TESTS =================
//
//    @Test
//    void applyDiscounts_success_withValidPoints() {
//        // Given - Redeem 50 points = 50,000 VND discount (25% of 200,000)
//        int pointsToRedeem = 50;
//
//        // When
//        Booking result = discountService.applyDiscounts(booking, pointsToRedeem);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(BigDecimal.valueOf(50000), result.getDiscount());
//        assertEquals(BigDecimal.valueOf(150000), result.getTotalAmount());
//    }
//
//    @Test
//    void applyDiscounts_success_withMaxAllowedDiscount() {
//        // Given - Redeem 100 points = 100,000 VND (50% of 200,000) - maximum allowed
//        int pointsToRedeem = 100;
//
//        // When
//        Booking result = discountService.applyDiscounts(booking, pointsToRedeem);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(BigDecimal.valueOf(100000), result.getDiscount());
//        assertEquals(BigDecimal.valueOf(100000), result.getTotalAmount());
//    }
//
//    @Test
//    void applyDiscounts_throwsException_whenExceedsMaxDiscount() {
//        // Given - Try to redeem 101 points = 101,000 VND (>50% of 200,000)
//        int pointsToRedeem = 101;
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class, () -> discountService.applyDiscounts(booking, pointsToRedeem));
//        assertEquals("Discount exceeds maximum allowed limit.", exception.getMessage());
//    }
//
//    @Test
//    void applyDiscounts_throwsException_whenExceedsFiftyPercent() {
//        // Given - Try to redeem 150 points = 150,000 VND (75% of 200,000)
//        int pointsToRedeem = 150;
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class, () -> discountService.applyDiscounts(booking, pointsToRedeem));
//        assertEquals("Discount exceeds maximum allowed limit.", exception.getMessage());
//    }
//
//    @Test
//    void applyDiscounts_success_withZeroPoints() {
//        // Given
//        int pointsToRedeem = 0;
//
//        // When
//        Booking result = discountService.applyDiscounts(booking, pointsToRedeem);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(BigDecimal.valueOf(0), result.getDiscount());
//        assertEquals(BigDecimal.valueOf(200000), result.getTotalAmount());
//    }
//
//    @Test
//    void applyDiscounts_success_withSmallDiscount() {
//        // Given - Redeem 10 points = 10,000 VND (5% of 200,000)
//        int pointsToRedeem = 10;
//
//        // When
//        Booking result = discountService.applyDiscounts(booking, pointsToRedeem);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(BigDecimal.valueOf(10000), result.getDiscount());
//        assertEquals(BigDecimal.valueOf(190000), result.getTotalAmount());
//    }
//
//    @Test
//    void applyDiscounts_calculatesCorrectly_withHighSubtotal() {
//        // Given - Booking with 1,000,000 VND subtotal
//        booking.setSubtotal(BigDecimal.valueOf(1000000));
//        booking.setTotalAmount(BigDecimal.valueOf(1000000));
//        int pointsToRedeem = 500; // 500,000 VND = 50%
//
//        // When
//        Booking result = discountService.applyDiscounts(booking, pointsToRedeem);
//
//        // Then
//        assertEquals(BigDecimal.valueOf(500000), result.getDiscount());
//        assertEquals(BigDecimal.valueOf(500000), result.getTotalAmount());
//    }
//
//    @Test
//    void applyDiscounts_throwsException_withHighSubtotal_exceedsMax() {
//        // Given - Booking with 1,000,000 VND subtotal
//        booking.setSubtotal(BigDecimal.valueOf(1000000));
//        booking.setTotalAmount(BigDecimal.valueOf(1000000));
//        int pointsToRedeem = 501; // 501,000 VND = 50.1%
//
//        // When & Then
//        assertThrows(IllegalArgumentException.class, () -> discountService.applyDiscounts(booking, pointsToRedeem));
//    }
//
//    // ================= CALCULATE EARNED POINTS TESTS =================
//
//    @Test
//    void calculateEarnedPoints_success_withStandardAmount() {
//        // Given - Total amount of 200,000 VND
//        // Rate: 1 point per 20,000 VND
//        BigDecimal totalAmount = BigDecimal.valueOf(200000);
//
//        // When
//        int earnedPoints = discountService.calculateEarnedPoints(totalAmount);
//
//        // Then
//        assertEquals(10, earnedPoints); // 200,000 / 20,000 = 10 points
//    }
//
//    @Test
//    void calculateEarnedPoints_success_withLargeAmount() {
//        // Given - Total amount of 1,000,000 VND
//        BigDecimal totalAmount = BigDecimal.valueOf(1000000);
//
//        // When
//        int earnedPoints = discountService.calculateEarnedPoints(totalAmount);
//
//        // Then
//        assertEquals(50, earnedPoints); // 1,000,000 / 20,000 = 50 points
//    }
//
//    @Test
//    void calculateEarnedPoints_returnsZero_withSmallAmount() {
//        // Given - Total amount of 15,000 VND (less than 20,000)
//        BigDecimal totalAmount = BigDecimal.valueOf(15000);
//
//        // When
//        int earnedPoints = discountService.calculateEarnedPoints(totalAmount);
//
//        // Then
//        assertEquals(0, earnedPoints); // 15,000 / 20,000 = 0.75 -> 0 points
//    }
//
//    @Test
//    void calculateEarnedPoints_truncatesDecimal() {
//        // Given - Total amount of 50,000 VND
//        BigDecimal totalAmount = BigDecimal.valueOf(50000);
//
//        // When
//        int earnedPoints = discountService.calculateEarnedPoints(totalAmount);
//
//        // Then
//        assertEquals(2, earnedPoints); // 50,000 / 20,000 = 2.5 -> 2 points (truncated)
//    }
//
//    @Test
//    void calculateEarnedPoints_returnsZero_withZeroAmount() {
//        // Given
//        BigDecimal totalAmount = BigDecimal.ZERO;
//
//        // When
//        int earnedPoints = discountService.calculateEarnedPoints(totalAmount);
//
//        // Then
//        assertEquals(0, earnedPoints);
//    }
//
//    @Test
//    void calculateEarnedPoints_success_withExactMultiple() {
//        // Given - Total amount of 100,000 VND (exact multiple of 20,000)
//        BigDecimal totalAmount = BigDecimal.valueOf(100000);
//
//        // When
//        int earnedPoints = discountService.calculateEarnedPoints(totalAmount);
//
//        // Then
//        assertEquals(5, earnedPoints); // 100,000 / 20,000 = 5 points
//    }
//
//    // ================= CALCULATE DISCOUNT POINTS TESTS =================
//
//    @Test
//    void calculateDiscountPoints_success_withStandardDiscount() {
//        // Given - Discount amount of 50,000 VND
//        // Rate: 1 point per 1,000 VND
//        BigDecimal discountAmount = BigDecimal.valueOf(50000);
//
//        // When
//        int discountPoints = discountService.caculateDiscountPoints(discountAmount);
//
//        // Then
//        assertEquals(50, discountPoints); // 50,000 / 1,000 = 50 points
//    }
//
//    @Test
//    void calculateDiscountPoints_success_withLargeDiscount() {
//        // Given - Discount amount of 500,000 VND
//        BigDecimal discountAmount = BigDecimal.valueOf(500000);
//
//        // When
//        int discountPoints = discountService.caculateDiscountPoints(discountAmount);
//
//        // Then
//        assertEquals(500, discountPoints); // 500,000 / 1,000 = 500 points
//    }
//
//    @Test
//    void calculateDiscountPoints_returnsZero_withSmallDiscount() {
//        // Given - Discount amount of 500 VND (less than 1,000)
//        BigDecimal discountAmount = BigDecimal.valueOf(500);
//
//        // When
//        int discountPoints = discountService.caculateDiscountPoints(discountAmount);
//
//        // Then
//        assertEquals(0, discountPoints); // 500 / 1,000 = 0.5 -> 0 points
//    }
//
//    @Test
//    void calculateDiscountPoints_truncatesDecimal() {
//        // Given - Discount amount of 15,500 VND
//        BigDecimal discountAmount = BigDecimal.valueOf(15500);
//
//        // When
//        int discountPoints = discountService.caculateDiscountPoints(discountAmount);
//
//        // Then
//        assertEquals(15, discountPoints); // 15,500 / 1,000 = 15.5 -> 15 points (truncated)
//    }
//
//    @Test
//    void calculateDiscountPoints_returnsZero_withZeroDiscount() {
//        // Given
//        BigDecimal discountAmount = BigDecimal.ZERO;
//
//        // When
//        int discountPoints = discountService.caculateDiscountPoints(discountAmount);
//
//        // Then
//        assertEquals(0, discountPoints);
//    }
//
//    @Test
//    void calculateDiscountPoints_success_withExactMultiple() {
//        // Given - Discount amount of 100,000 VND (exact multiple of 1,000)
//        BigDecimal discountAmount = BigDecimal.valueOf(100000);
//
//        // When
//        int discountPoints = discountService.caculateDiscountPoints(discountAmount);
//
//        // Then
//        assertEquals(100, discountPoints); // 100,000 / 1,000 = 100 points
//    }
//
//    // ================= INTEGRATION TESTS =================
//
//    @Test
//    void pointsCalculation_integration_fullFlow() {
//        // Given - Customer books 200,000 VND worth of tickets
//        BigDecimal totalAmount = BigDecimal.valueOf(200000);
//
//        // When - Calculate earned points
//        int earnedPoints = discountService.calculateEarnedPoints(totalAmount);
//
//        // Then - Customer earns 10 points
//        assertEquals(10, earnedPoints);
//
//        // Given - Customer uses all 10 points for discount on next booking
//        booking.setSubtotal(BigDecimal.valueOf(300000));
//        booking.setTotalAmount(BigDecimal.valueOf(300000));
//        Booking discountedBooking = discountService.applyDiscounts(booking, 10);
//
//        // Then - 10 points = 10,000 VND discount
//        assertEquals(BigDecimal.valueOf(10000), discountedBooking.getDiscount());
//        assertEquals(BigDecimal.valueOf(290000), discountedBooking.getTotalAmount());
//
//        // When - Calculate points deducted
//        int pointsDeducted = discountService.caculateDiscountPoints(discountedBooking.getDiscount());
//
//        // Then - 10 points were deducted
//        assertEquals(10, pointsDeducted);
//    }
//
//    @Test
//    void pointsCalculation_integration_maxDiscountScenario() {
//        // Given - Customer has 500 loyalty points and books 400,000 VND
//        booking.setSubtotal(BigDecimal.valueOf(400000));
//        booking.setTotalAmount(BigDecimal.valueOf(400000));
//
//        // When - Try to use 200 points (max 50% = 200,000 VND)
//        Booking result = discountService.applyDiscounts(booking, 200);
//
//        // Then
//        assertEquals(BigDecimal.valueOf(200000), result.getDiscount());
//        assertEquals(BigDecimal.valueOf(200000), result.getTotalAmount());
//
//        // When - Calculate points used
//        int pointsUsed = discountService.caculateDiscountPoints(result.getDiscount());
//
//        // Then
//        assertEquals(200, pointsUsed);
//    }
// }
