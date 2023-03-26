package com.example.g2gcalculator.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

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

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "realm_id")
    private Realm realm;

    public void setRealm(Realm realm) {
        realm.getPrices().add(this);
        this.realm = realm;
    }
}