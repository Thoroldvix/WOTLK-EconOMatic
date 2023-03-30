package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.server.Server;
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
@Table(name = "price")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "value")
    private BigDecimal value;
    @Column(name = "updated_at")
    @CreationTimestamp
    private LocalDateTime updatedAt;
    @Column(name = "currency")
    private String currency;
    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    public void setServer(Server server) {
        server.getPrices().add(this);
        this.server = server;
    }
}