package com.example.g2gcalculator.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

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
    private String name;
    private UUID g2gId;
    @OneToMany
    @JoinColumn(name = "region_name")
    @ToString.Exclude
    private List<Realm> realms;

}