package com.example.g2gcalculator.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "realm")
public class Realm {
    @Id
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Faction faction;

    @ManyToOne
    private Region region;

    @Enumerated(EnumType.STRING)
    private GameVersion gameVersion;

    @OneToMany
    @JoinColumn(name = "realm_id")
    @ToString.Exclude
    private List<Price> prices;

    @OneToMany
    @JoinColumn(name = "realm_id")
    @ToString.Exclude
    private List<AuctionHouse> auctionHouses;
}