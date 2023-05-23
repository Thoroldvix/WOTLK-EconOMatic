package com.thoroldvix.g2gcalculator.item.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@JsonDeserialize(using = ItemDeserializer.class)
public class Item {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String icon;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemQuality quality;

    @Column(nullable = false)
    private String uniqueName;

    @Column(nullable = false)
    private int itemLevel;

    @Column(nullable = false)
    private int requiredLevel;

    @Column(nullable = false)
    private long sellPrice;


    @Column(nullable = false)
    private String itemLink;
}
