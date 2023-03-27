package com.example.g2gcalculator.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private Realm realm;
}