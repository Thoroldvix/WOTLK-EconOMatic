package com.thoroldvix.pricepal.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Locale;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Faction faction;

    @Column(nullable = false)
    private Locale locale;

    @Column(nullable = false)
    private String type;

    @OneToMany(mappedBy = "server", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ServerPrice> serverPrices;

    @Column(nullable = false, updatable = false)
    private String uniqueName;

    @OneToMany(mappedBy = "server", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Population> populations;

    public void setPrice(ServerPrice serverPrice) {
        serverPrices.add(serverPrice);
        serverPrice.setServer(this);
    }

    public void addPopulation(Population population) {
        populations.add(population);
        population.setServer(this);
    }
}