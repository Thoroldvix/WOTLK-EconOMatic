package com.thoroldvix.g2gcalculator.item.entity;

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
    Integer id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, unique = true)
    String icon;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ItemType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ItemQuality quality;
}
