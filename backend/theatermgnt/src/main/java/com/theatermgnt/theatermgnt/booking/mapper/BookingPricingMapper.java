package com.theatermgnt.theatermgnt.booking.mapper;

import com.theatermgnt.theatermgnt.booking.dto.response.BookingPricingResponse;
import com.theatermgnt.theatermgnt.booking.entity.Booking;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface BookingPricingMapper {
    default BookingPricingResponse toPricingResponse(
            Booking booking) {
        BookingPricingResponse res = new BookingPricingResponse();

        res.setBookingId(booking.getId());
        res.setSubTotal(booking.getSubtotal());
        res.setDiscount(
                booking.getDiscount() != null
                        ? booking.getDiscount()
                        : BigDecimal.ZERO
        );
        res.setTotalAmount(booking.getTotalAmount());

        return res;
    }
}
