package com.thoroldvix.g2gcalculator.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "auction_house")
public class AuctionHouse {

    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "realm_id", unique = true)
    @ToString.Exclude
    private Realm realm;
}