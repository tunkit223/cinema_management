package com.theatermgnt.theatermgnt.priceConfig.mapper;

import com.theatermgnt.theatermgnt.priceConfig.dto.request.PriceConfigCreationRequest;
import com.theatermgnt.theatermgnt.priceConfig.dto.request.PriceConfigUpdateRequest;
import com.theatermgnt.theatermgnt.priceConfig.dto.response.PriceConfigResponse;
import com.theatermgnt.theatermgnt.priceConfig.entity.PriceConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PriceConfigMapper {
    PriceConfig toPriceConfig(PriceConfigCreationRequest request);

    @Mapping(target = "seatTypeName", source = "seatType.typeName")
    PriceConfigResponse toPriceConfigResponse(PriceConfig priceConfig);

    void updatePriceConfig(@MappingTarget PriceConfig priceConfig, PriceConfigUpdateRequest request);
}
