package com.thoroldvix.economatic.item;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ItemType type;

    @Column(name = "quality", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ItemQuality quality;

    @Column(name = "slot", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ItemSlot slot;

    @Column(name = "unique_name", nullable = false, insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private String uniqueName;

    @Column(name = "vendor_price", nullable = false)
    private long vendorPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
