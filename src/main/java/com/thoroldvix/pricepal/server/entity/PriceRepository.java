package com.thoroldvix.pricepal.server.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<ServerPrice, Integer> {
    @Query("select p from ServerPrice p where p.server = ?1 order by p.lastUpdated desc limit 1")
    Optional<ServerPrice> findMostRecentPriceByServer(Server server);

    Page<ServerPrice> findAllByServer(Server server, Pageable pageable);

    List<ServerPrice> findAllByServer(Server server);

    @Query("select avg(p.value) from ServerPrice p where p.server = ?1 and p.lastUpdated >= ?2")
    Optional<Double> findAvgPriceForServer(Server server, LocalDateTime startDate);

    @Query("select avg(p.value) from ServerPrice p where p.server.region = ?1 and p.lastUpdated >= ?2")
    Optional<Double> findAvgPriceForRegion(Region region, LocalDateTime startDate);
}