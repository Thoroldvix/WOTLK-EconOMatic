package com.thoroldvix.g2gcalculator.server.entity;

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
@Table(name = "server")
public class Server {
    @Id
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Faction faction;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    private Region region;


    @Column(name = "type", nullable = false)
    private String type;

    @OneToMany(mappedBy = "server", orphanRemoval = true, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Price> prices;


    public void setPrice(Price price) {
        prices.add(price);
        price.setServer(this);
    }
}