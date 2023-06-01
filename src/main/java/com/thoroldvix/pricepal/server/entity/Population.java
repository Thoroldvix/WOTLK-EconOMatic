package com.thoroldvix.pricepal.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

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
    private Long id;

    @Column(nullable = false)
    private int population;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    public void setServer(Server server) {
        server.getPopulations().add(this);
        this.server = server;
    }
}
