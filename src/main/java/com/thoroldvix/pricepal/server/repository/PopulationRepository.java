package com.thoroldvix.pricepal.server.repository;

import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Population;
import com.thoroldvix.pricepal.server.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PopulationRepository extends JpaRepository<Population, Long> {

    @Query("select p " +
           "from Population p " +
           "where p.server.uniqueName = ?1 " +
           "and p.updatedAt = (select max(p2.updatedAt) from Population p2 where p2.server.uniqueName = ?1)")
    Optional<Population> findRecentByUniqueServerName(String serverName);

    @Query("select max(p.updatedAt)" +
           " from Population p" +
           " where p.server.id = ?1")
    Optional<Population> findRecentByUniqueServerId(int serverId);

    @Query("select sum(p.population) " +
           "from Population p " +
           "where p.server.faction = ?1 " +
           "and p.updatedAt = (select max(p2.updatedAt) " +
           "from Population p2 where p2.server = p.server" +
           " and p2.server.faction = ?1)")
    Optional<Integer> findTotalPopulationForFaction(Faction faction);

    @Query("select sum(p.population) " +
           "from Population p " +
           "where p.server.region = ?1 " +
           "and p.updatedAt = (select max(p2.updatedAt) " +
           "from Population p2 where p2.server = p.server" +
           " and p2.server.region = ?1)")
    Optional<Integer> findTotalPopulationForRegion(Region region);
}
