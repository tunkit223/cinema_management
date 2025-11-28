package com.theatermgnt.theatermgnt.equipment.repository;

import com.theatermgnt.theatermgnt.equipment.entity.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, String> {
    Optional<EquipmentCategory> findByName(String name);
    boolean existsByName(String name);
}
