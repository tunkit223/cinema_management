package com.theatermgnt.theatermgnt.combo.repository;

import com.theatermgnt.theatermgnt.combo.entity.ComboItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComboItemRepository extends JpaRepository<ComboItem, String> {
    List<ComboItem> findByComboId(String comboId);

    boolean existsByNameAndComboId(String name, String comboId);
}
