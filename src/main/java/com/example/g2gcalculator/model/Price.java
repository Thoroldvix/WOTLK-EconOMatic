package com.example.g2gcalculator.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
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

    private BigDecimal value;

    private LocalDateTime createdAt;

    @ManyToOne
    private Realm realm;

    public void setRealm(Realm realm) {
        realm.getPrices().add(this);
        this.realm = realm;
    }
}