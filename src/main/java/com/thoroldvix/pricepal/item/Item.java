package com.thoroldvix.pricepal.item;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@ToString
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

    @Column(nullable = false)
    private String uniqueName;

    @Column(nullable = false)
    private long sellPrice;

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
