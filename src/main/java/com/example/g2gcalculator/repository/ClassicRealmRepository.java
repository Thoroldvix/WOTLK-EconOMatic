package com.example.g2gcalculator.repository;

import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.model.Realm;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassicRealmRepository extends JpaRepository<Realm, Integer> {


    @Query("select r from Realm r  where lower(r.name) = lower(?1) and r.faction = ?2")
    Optional<Realm> findByNameAndFaction(String name, Faction faction);
}