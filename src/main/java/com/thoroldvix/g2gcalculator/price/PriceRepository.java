package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.server.Region;
import com.thoroldvix.g2gcalculator.server.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Integer> {
    @Query("select p from Price p where p.server = ?1 order by p.updatedAt desc limit 1")
    Optional<Price> findMostRecentPriceByServer(Server server);

    Page<Price> findAllByServer(Server server, Pageable pageable);

    List<Price> findAllByServer(Server server);

    @Query("select avg(p.value) from Price p where p.server = ?1 and p.updatedAt >= ?2")
    Optional<Double> findAvgPriceForServer(Server server, LocalDateTime startDate);

    @Query("select avg(p.value) from Price p where p.server.region = ?1 and p.updatedAt >= ?2")
    Optional<Double> findAvgPriceForRegion(Region region, LocalDateTime startDate);
}