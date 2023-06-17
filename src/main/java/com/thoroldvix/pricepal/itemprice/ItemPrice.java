package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.item.Item;
import com.thoroldvix.pricepal.server.Server;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "item_price")
@AllArgsConstructor
public class ItemPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long minBuyout;

    private long historicalValue;

    private long marketValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    @ToString.Exclude
    private Server server;

    private int quantity;

    private int numAuctions;

    @CreationTimestamp
    private LocalDateTime updatedAt;

}
