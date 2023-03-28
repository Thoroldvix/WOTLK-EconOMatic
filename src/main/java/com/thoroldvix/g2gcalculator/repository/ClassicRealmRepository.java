package com.thoroldvix.g2gcalculator.repository;

import com.thoroldvix.g2gcalculator.model.Faction;
import com.thoroldvix.g2gcalculator.model.Realm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassicRealmRepository extends JpaRepository<Realm, Integer> {


    @Query("select r from Realm r  where lower(r.name) = lower(?1) and r.faction = ?2")
    Optional<Realm> findByNameAndFaction(String name, Faction faction);
}