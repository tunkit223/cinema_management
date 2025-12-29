package com.theatermgnt.theatermgnt.priceConfig.service;

import java.util.List;

import com.theatermgnt.theatermgnt.common.enums.DayType;
import com.theatermgnt.theatermgnt.common.enums.TimeSlot;
import org.springframework.stereotype.Service;

import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
import com.theatermgnt.theatermgnt.priceConfig.dto.request.PriceConfigCreationRequest;
import com.theatermgnt.theatermgnt.priceConfig.dto.request.PriceConfigUpdateRequest;
import com.theatermgnt.theatermgnt.priceConfig.dto.response.PriceConfigResponse;
import com.theatermgnt.theatermgnt.priceConfig.entity.PriceConfig;
import com.theatermgnt.theatermgnt.priceConfig.mapper.PriceConfigMapper;
import com.theatermgnt.theatermgnt.priceConfig.repository.PriceConfigRepository;
import com.theatermgnt.theatermgnt.seatType.entity.SeatType;
import com.theatermgnt.theatermgnt.seatType.repository.SeatTypeRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PriceConfigService {
    PriceConfigRepository priceConfigRepository;
    SeatTypeRepository seatTypeRepository;
    PriceConfigMapper priceConfigMapper;

    public PriceConfigResponse createPriceConfig(PriceConfigCreationRequest request) {
        SeatType seatType = seatTypeRepository
                .findById(request.getSeatTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.SEATTYPE_NOT_EXISTED));


        DayType dayType = DayType.valueOf(request.getDayType());
        TimeSlot timeSlot = TimeSlot.valueOf(request.getTimeSlot());

        var existingConfig = priceConfigRepository.findBySeatTypeIdAndDayTypeAndTimeSlot(
                request.getSeatTypeId(),
                dayType,
                timeSlot);

        if (existingConfig.isPresent()) {
            PriceConfig priceConfig = existingConfig.get();
            priceConfig.setPrice(request.getPrice());
            return priceConfigMapper.toPriceConfigResponse(priceConfigRepository.save(priceConfig));
        }

        PriceConfig priceConfig = priceConfigMapper.toPriceConfig(request);
        priceConfig.setSeatType(seatType);

        return priceConfigMapper.toPriceConfigResponse(priceConfigRepository.save(priceConfig));
    }

    public List<PriceConfigResponse> getPriceConfigsBySeatType(String seatTypeId) {
        return priceConfigRepository.findBySeatTypeId(seatTypeId).stream()
                .map(priceConfigMapper::toPriceConfigResponse)
                .toList();
    }

    public List<PriceConfigResponse> getPriceConfigs() {
        return priceConfigRepository.findAll().stream()
                .map(priceConfigMapper::toPriceConfigResponse)
                .toList();
    }

    public PriceConfigResponse getPriceConfig(String priceConfigId) {
        PriceConfig priceConfig = priceConfigRepository
                .findById(priceConfigId)
                .orElseThrow(() -> new AppException(ErrorCode.PRICECONFIG_NOT_EXISTED));
        return priceConfigMapper.toPriceConfigResponse(priceConfig);
    }

    public PriceConfigResponse updatePriceConfig(String priceConfigId, PriceConfigUpdateRequest request) {
        PriceConfig priceConfig = priceConfigRepository
                .findById(priceConfigId)
                .orElseThrow(() -> new AppException(ErrorCode.PRICECONFIG_NOT_EXISTED));

        priceConfig.setPrice(request.getPrice());
        return priceConfigMapper.toPriceConfigResponse(priceConfigRepository.save(priceConfig));
    }

    public void deletePriceConfig(String priceConfigId) {
        PriceConfig priceConfig = priceConfigRepository
                .findById(priceConfigId)
                .orElseThrow(() -> new AppException(ErrorCode.PRICECONFIG_NOT_EXISTED));
        priceConfigRepository.delete(priceConfig);
    }
}
