package com.thoroldvix.g2gcalculator.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@Table(name = "population")
@AllArgsConstructor
public class Population {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @OneToMany(mappedBy = "population", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Server> servers = new ArrayList<>();

    @Column(nullable = false)
    private int popAlliance;

    @Column(nullable = false)
    private int popHorde;

    public void addServer(Server server) {
        servers.add(server);
        server.setPopulation(this);
    }
}
