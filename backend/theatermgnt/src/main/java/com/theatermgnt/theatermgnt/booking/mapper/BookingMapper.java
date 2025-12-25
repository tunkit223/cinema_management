package com.theatermgnt.theatermgnt.booking.mapper;

import org.mapstruct.Mapper;

import com.theatermgnt.theatermgnt.booking.dto.response.CreateBookingResponse;
import com.theatermgnt.theatermgnt.booking.entity.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    CreateBookingResponse toCreateBookingResponse(Booking booking);
}
