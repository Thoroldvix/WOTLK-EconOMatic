package com.thoroldvix.pricepal.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "server_price")
public class ServerPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    public void setServer(Server server) {
        server.getServerPrices().add(this);
        this.server = server;
    }
}