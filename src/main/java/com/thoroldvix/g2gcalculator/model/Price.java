package com.thoroldvix.g2gcalculator.model;

import jakarta.persistence.*;
import lombok.*;

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
    private LocalDateTime updatedAt;

    @Column(name = "currency")
    private String currency;

    @ManyToOne
    @JoinColumn(name = "realm_id")
    private Realm realm;

    public void setRealm(Realm realm) {
        realm.getPrices().add(this);
        this.realm = realm;
    }
}