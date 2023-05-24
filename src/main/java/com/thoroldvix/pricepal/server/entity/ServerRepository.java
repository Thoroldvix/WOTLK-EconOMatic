package com.thoroldvix.pricepal.server.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Integer> {


    @Query("select r from Server r  where lower(r.name) = lower(?1) and r.faction = ?2")
    Optional<Server> findByNameAndFaction(String name, Faction faction);

    @Query("SELECT s FROM Server s WHERE s.region = ?1")
    List<Server> findAllByRegion(Region region);

    List<Server> findAllByFaction(Faction faction);

    Optional<Server> findByUniqueName(String uniqueName);

    List<Server> findByName(String serverName);

    @Query("SELECT s " +
           "FROM Server s " +
           "WHERE lower( s.name) " +
           "like lower(concat('%', ?1, '%' ))")
    List<Server> searchByName(String name);
}