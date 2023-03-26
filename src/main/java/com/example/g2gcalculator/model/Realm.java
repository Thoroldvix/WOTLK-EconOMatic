package com.example.g2gcalculator.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

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

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private GameVersion gameVersion;

    @OneToMany(mappedBy = "realm", orphanRemoval = true, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Price> prices;

    @OneToOne(mappedBy = "realm", cascade = CascadeType.ALL)
    private AuctionHouse auctionHouse;

    public void setPrice(Price price) {
        prices.add(price);
        price.setRealm(this);
    }

    public void setAuctionHouse(AuctionHouse auctionHouse) {
        this.auctionHouse = auctionHouse;
        auctionHouse.setRealm(this);
    }
}