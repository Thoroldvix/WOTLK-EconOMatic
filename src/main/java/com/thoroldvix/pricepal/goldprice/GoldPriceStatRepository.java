package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.shared.StatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface GoldPriceStatRepository extends JpaRepository<GoldPrice, Long> {
    String statSql = """
            SELECT
            (SELECT AVG(gp.price) FROM gold_prices gp) AS mean,
                                       (SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY gp.price) FROM gold_prices gp) AS median,
                                       (SELECT gp.id FROM gold_prices gp WHERE gp.price = (SELECT MIN(gp1.price) FROM gold_prices gp1) ORDER BY gp.updated_at DESC LIMIT 1) AS minId,
                                       (SELECT gp.id FROM gold_prices gp WHERE gp.price = (SELECT MAX(gp1.price) FROM gold_prices gp1) ORDER BY gp.updated_at DESC LIMIT 1) AS maxId,
                                       (SELECT COUNT(gp.id) FROM gold_prices gp) AS count;
            """;

    @Query(value = """
            WITH gold_prices AS (SELECT gp.price, gp.id, gp.updated_at
            FROM gold_price gp
            JOIN server s ON s.id = gp.server_id
            WHERE s.region = ?1
    )
    """ + statSql, nativeQuery = true)
    StatsProjection findForRegion(int region);

    @Query(value = """
            WITH gold_prices AS (SELECT gp.price, gp.id, gp.updated_at
            FROM gold_price gp
            JOIN server s ON s.id = gp.server_id
            WHERE s.region = ?1
    )
    """ + statSql, nativeQuery = true)
    StatsProjection findForFaction(int faction);


    @Query(value = """
            WITH gold_prices AS (SELECT gp.price, gp.id, gp.updated_at
            FROM gold_price gp
            WHERE gp.updated_at >= ?1 AND gp.updated_at <= ?2
    )
      """ + statSql, nativeQuery = true)
    StatsProjection findStatsForAll(LocalDateTime start, LocalDateTime end);

    @Query(value = """
            WITH gold_prices AS (SELECT gp.price, gp.id, gp.updated_at
            FROM gold_price gp
            WHERE gp.server_id = ?1
    )
 """ + statSql, nativeQuery = true)
    StatsProjection findStatsForServer(int serverId);
}
