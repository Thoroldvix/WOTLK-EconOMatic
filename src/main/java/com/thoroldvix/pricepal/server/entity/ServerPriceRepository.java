package com.thoroldvix.pricepal.server.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ServerPriceRepository extends JpaRepository<ServerPrice, Long> {
    @Query("select p from ServerPrice p where p.server = ?1 order by p.updatedAt desc limit 1")
    Optional<ServerPrice> findMostRecentPriceByServer(Server server);

    @Query("select avg(p.value) from ServerPrice p where p.server.uniqueName = ?1 and p.updatedAt >= ?2")
    Optional<BigDecimal> findAvgPriceByUniqueServerName(String serverName, LocalDateTime averageTimePeriod);

    @Query("select avg(p.value) from ServerPrice p where p.server.region = ?1 and p.updatedAt >= ?2")
    Optional<BigDecimal> findAvgPriceByRegion(Region region, LocalDateTime averageTimePeriod);

    @Query("select p from ServerPrice p where p.server.region = ?1 and p.updatedAt in " +
           "(select max(p2.updatedAt) from ServerPrice p2 where p2.server.region = ?1 group by p2.server)")
    Page<ServerPrice> findAllPricesByRegion(Region region, Pageable pageable);

    @Query("select p from ServerPrice p where p.server.faction = ?1 and p.updatedAt in " +
           "(select max(p2.updatedAt) from ServerPrice p2 where p2.server.faction = ?1 group by p2.server)")
    Page<ServerPrice> findAllPricesByFaction(Faction faction, Pageable pageable);

    @Query("select avg(p.value) from ServerPrice p where p.server.faction = ?1 and p.updatedAt >= ?2")
    Optional<BigDecimal> findAvgPriceByFaction(Faction faction, LocalDateTime averageTimePeriod);

    @Query("select p from ServerPrice p where p.server.id = ?1")
    Page<ServerPrice> findAllPricesByServerId(int serverId, Pageable pageable);

    @Query("select p from ServerPrice p where p.server.uniqueName = ?1")
    Page<ServerPrice> findAllByUniqueServerName(String serverName, Pageable pageable);

    @Query("select max(p.updatedAt) from ServerPrice p where p.server.id = ?1")
    Optional<ServerPrice> findMostRecentPriceByServerId(int serverId);

    @Query("select max(p.updatedAt) from ServerPrice p where p.server.uniqueName = ?1")
    Optional<ServerPrice> findMostRecentPriceByUniqueServerName(String serverName);
    @Query("select avg(p.value) from ServerPrice p where p.server.id = ?1 and p.updatedAt >= ?2")
    Optional<BigDecimal> findAvgPriceByServerId(int serverId, LocalDateTime averageTimePeriod);
}