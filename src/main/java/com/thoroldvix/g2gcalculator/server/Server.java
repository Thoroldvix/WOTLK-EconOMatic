package com.thoroldvix.g2gcalculator.server;

import com.thoroldvix.g2gcalculator.price.Price;
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

    @OneToMany(mappedBy = "server", orphanRemoval = true, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Price> prices;

    public void setPrice(Price price) {
        prices.add(price);
        price.setServer(this);
    }
}