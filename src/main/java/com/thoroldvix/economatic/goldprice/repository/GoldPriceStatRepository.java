package com.thoroldvix.economatic.goldprice.repository;

import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.shared.StatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface GoldPriceStatRepository extends JpaRepository<GoldPrice, Long> {
    String STAT_SQL = """
            SELECT
            (SELECT CAST(AVG(gp.value) AS DECIMAL(7, 6)) FROM gold_prices gp) AS mean,
            CAST((SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY gp.value) FROM gold_prices gp)  AS DECIMAL(7, 6)) AS median,
            (SELECT gp.id FROM gold_prices gp WHERE gp.value = (SELECT MIN(gp1.value) FROM gold_prices gp1) ORDER BY gp.updated_at DESC LIMIT 1) AS minId,
            (SELECT gp.id FROM gold_prices gp WHERE gp.value = (SELECT MAX(gp1.value) FROM gold_prices gp1) ORDER BY gp.updated_at DESC LIMIT 1) AS maxId,
            (SELECT COUNT(gp.id) FROM gold_prices gp) AS count;
            """;

    @Query(value = """
            WITH gold_prices AS (SELECT gp.value, gp.id, gp.updated_at
            FROM gold_price gp
            JOIN server s ON s.id = gp.server_id
            WHERE s.region = ?1 and gp.updated_at >= ?2 and gp.updated_at <= ?3
    )
    """ + STAT_SQL, nativeQuery = true)
    StatsProjection findForRegion(int region, LocalDateTime start, LocalDateTime end);

    @Query(value = """
            WITH gold_prices AS (SELECT gp.value, gp.id, gp.updated_at
            FROM gold_price gp
            JOIN server s ON s.id = gp.server_id
            WHERE s.faction = ?1 and gp.updated_at >= ?2 and gp.updated_at <= ?3
    )
    """ + STAT_SQL, nativeQuery = true)
    StatsProjection findForFaction(int faction, LocalDateTime start, LocalDateTime end);


    @Query(value = """
            WITH gold_prices AS (SELECT gp.value, gp.id, gp.updated_at
            FROM gold_price gp
            WHERE gp.updated_at >= ?1 AND gp.updated_at <= ?2
    )
      """ + STAT_SQL, nativeQuery = true)
    StatsProjection findStatsForAll(LocalDateTime start, LocalDateTime end);

    @Query(value = """
            WITH gold_prices AS (SELECT gp.value, gp.id, gp.updated_at
            FROM gold_price gp
            WHERE gp.server_id = ?1 and gp.updated_at >= ?2 and gp.updated_at <= ?3
    )
 """ + STAT_SQL, nativeQuery = true)
    StatsProjection findStatsForServer(int serverId, LocalDateTime start, LocalDateTime end);
}
