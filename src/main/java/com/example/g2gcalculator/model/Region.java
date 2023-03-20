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
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany
    @JoinColumn(name = "region_id")
    @ToString.Exclude
    private List<Realm> realms;

    @Enumerated(value = EnumType.STRING)
    public GameVersion version;


}