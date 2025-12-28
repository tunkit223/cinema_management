package com.theatermgnt.theatermgnt.priceConfig.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.theatermgnt.theatermgnt.common.enums.DayType;
import com.theatermgnt.theatermgnt.common.enums.TimeSlot;
import com.theatermgnt.theatermgnt.priceConfig.dto.request.PriceConfigCreationRequest;
import com.theatermgnt.theatermgnt.priceConfig.dto.request.PriceConfigUpdateRequest;
import com.theatermgnt.theatermgnt.priceConfig.dto.response.PriceConfigResponse;
import com.theatermgnt.theatermgnt.priceConfig.entity.PriceConfig;

@Mapper(componentModel = "spring", imports = {DayType.class, TimeSlot.class})
public interface PriceConfigMapper {
    
    @Mapping(target = "dayType", expression = "java(DayType.valueOf(request.getDayType()))")
    @Mapping(target = "timeSlot", expression = "java(TimeSlot.valueOf(request.getTimeSlot()))")
    @Mapping(target = "seatType", ignore = true)
    PriceConfig toPriceConfig(PriceConfigCreationRequest request);

    @Mapping(target = "seatTypeName", source = "seatType.typeName")
    PriceConfigResponse toPriceConfigResponse(PriceConfig priceConfig);
}
