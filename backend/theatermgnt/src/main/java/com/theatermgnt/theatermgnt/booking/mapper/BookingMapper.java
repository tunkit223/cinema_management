package com.theatermgnt.theatermgnt.booking.mapper;

import com.theatermgnt.theatermgnt.booking.dto.response.CreateBookingResponse;
import com.theatermgnt.theatermgnt.booking.entity.Booking;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    CreateBookingResponse toCreateBookingResponse(Booking booking);
}
