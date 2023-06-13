package com.thoroldvix.pricepal.item;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, unique = true)
    private String iconLink;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemQuality quality;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemSlot slot;

    @Column(nullable = false)
    private String uniqueName;

    @Column(nullable = false)
    private int itemLevel;

    @Column(nullable = false)
    private int requiredLevel;

    @Column(nullable = false)
    private long sellPrice;

    @Column(nullable = false)
    private String wowheadLink;

    @Column(nullable = false)
    private String itemLink;
}