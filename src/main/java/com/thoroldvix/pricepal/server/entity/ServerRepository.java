package com.thoroldvix.pricepal.server.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Integer> {
    List<Server> findAllByRegion(Region region);
    List<Server> findAllByFaction(Faction faction);
    Optional<Server> findByUniqueName(String uniqueName);
    List<Server> findByName(String serverName);

    @Query("select s " +
           "from Server s " +
           "where lower( s.name) " +
           "like lower(concat('%', ?1, '%' ))")
    List<Server> searchByName(String name);

}