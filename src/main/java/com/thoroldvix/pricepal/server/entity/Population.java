package com.thoroldvix.pricepal.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private int population;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    public void setServer(Server server) {
        server.getPopulations().add(this);
        this.server = server;
    }
}
