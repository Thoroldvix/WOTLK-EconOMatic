package com.thoroldvix.pricepal.server.entity;

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
    private List<ServerPrice> serverPrices;

    @Column(nullable = false, updatable = false)
    private String uniqueName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "population_id", nullable = false)
    private Population population;


    public void setPrice(ServerPrice serverPrice) {
        serverPrices.add(serverPrice);
        serverPrice.setServer(this);
    }
    public void setPopulation(Population population) {
        this.population = population;
        population.getServers().add(this);
    }
}