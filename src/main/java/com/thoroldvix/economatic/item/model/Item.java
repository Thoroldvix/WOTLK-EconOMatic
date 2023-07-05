package com.thoroldvix.economatic.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.util.Objects;

@Entity
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ItemType type;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ItemQuality quality;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ItemSlot slot;

    @Column(nullable = false, insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private String uniqueName;

    @Column(nullable = false)
    private long vendorPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Item item = (Item) o;
        return getId() != null && Objects.equals(getId(), item.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
