package com.theatermgnt.theatermgnt.combo.repository;

import com.theatermgnt.theatermgnt.combo.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComboRepository extends JpaRepository<Combo, String> {
    boolean existsByName(String name);
}
