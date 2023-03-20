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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    private Region region;

    @OneToMany
    @JoinColumn(name = "realm_id")
    @ToString.Exclude
    private List<Price> prices;

    @OneToMany
    @ToString.Exclude
    private List<AuctionHouse> auctionHouses;
}