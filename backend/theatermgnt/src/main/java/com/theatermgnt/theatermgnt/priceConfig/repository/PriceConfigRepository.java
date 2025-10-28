package com.theatermgnt.theatermgnt.priceConfig.repository;

import com.theatermgnt.theatermgnt.priceConfig.entity.PriceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceConfigRepository extends JpaRepository<PriceConfig, String> {
    List<PriceConfig> findBySeatTypeId(String seatTypeId);
}
