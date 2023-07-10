package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.item.Item;
import com.thoroldvix.economatic.server.Server;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@Getter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "item_price")
@AllArgsConstructor
public class ItemPrice {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_buyout")
    private long minBuyout;

    @Column(name = "historical_value")
    private long historicalValue;

    @Column(name = "market_value")
    private long marketValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    @ToString.Exclude
    private Server server;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "num_auctions")
    private int numAuctions;

    @Column(name = "updated_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ItemPrice itemPrice = (ItemPrice) o;
        return getId() != null && Objects.equals(getId(), itemPrice.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
