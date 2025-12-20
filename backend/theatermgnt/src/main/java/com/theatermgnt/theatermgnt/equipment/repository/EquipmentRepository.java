package com.theatermgnt.theatermgnt.equipment.repository;

import com.theatermgnt.theatermgnt.equipment.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, String> {
    List<Equipment> findByRoomId(String roomId);
    List<Equipment> findByCategoryId(String categoryId);
    List<Equipment> findByStatus(String status);
    List<Equipment> findByRoomIdAndStatus(String roomId, String status);
    List<Equipment> findByRoomIdAndCategoryId(String roomId, String categoryId);
    boolean existsBySerialNumber(String serialNumber);
}
