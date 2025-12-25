package com.theatermgnt.theatermgnt.combo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.theatermgnt.theatermgnt.combo.entity.ComboItem;

public interface ComboItemRepository extends JpaRepository<ComboItem, String> {
    List<ComboItem> findByComboId(String comboId);

    boolean existsByNameAndComboId(String name, String comboId);

    @Transactional
    @Modifying
    @Query("UPDATE ComboItem c SET c.deleted = true WHERE c.combo.id = :comboId")
    void softDeleteByComboId(String comboId);
}
