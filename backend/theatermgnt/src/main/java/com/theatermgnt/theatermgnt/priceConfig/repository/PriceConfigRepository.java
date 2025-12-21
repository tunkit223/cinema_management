package com.theatermgnt.theatermgnt.priceConfig.repository;

import com.theatermgnt.theatermgnt.common.enums.DayType;
import com.theatermgnt.theatermgnt.common.enums.TimeSlot;
import com.theatermgnt.theatermgnt.priceConfig.entity.PriceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface PriceConfigRepository extends JpaRepository<PriceConfig, String> {
    List<PriceConfig> findBySeatTypeId(String seatTypeId);

    PriceConfig getPriceBySeatTypeIdAndDayTypeAndTimeSlot(String seatTypeId, DayType dayType, TimeSlot timeSlot);
}
