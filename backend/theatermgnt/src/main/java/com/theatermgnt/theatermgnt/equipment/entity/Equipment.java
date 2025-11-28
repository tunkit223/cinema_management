package com.theatermgnt.theatermgnt.equipment.entity;

import com.theatermgnt.theatermgnt.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "equipments")
@SQLDelete(sql = "UPDATE equipments SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Equipment extends BaseEntity {
    @Column(nullable = false)
    String name;
    
    @Column(nullable = false)
    String categoryId;
    
    @Column(nullable = false)
    String roomId;
    
    String serialNumber;
    
    @Column(nullable = false)
    String status; // ACTIVE, MAINTENANCE, BROKEN
    
    LocalDate purchaseDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    EquipmentCategory category;
}
