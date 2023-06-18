package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.shared.StatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PopulationStatRepository extends JpaRepository<Population, Long> {

    String statSql = """
            (SELECT AVG(rp.population) FROM populations rp) AS mean,
                                       (SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY rp.population) FROM populations rp) AS median,
                                       (SELECT rp.id FROM populations rp WHERE rp.population = (SELECT MIN(rp1.population) FROM populations rp1) ORDER BY rp.updated_at DESC LIMIT 1) AS minId,
                                       (SELECT rp.id FROM populations rp WHERE rp.population = (SELECT MAX(rp1.population) FROM populations rp1) ORDER BY rp.updated_at DESC LIMIT 1) AS maxId,
                                       (SELECT COUNT(rp.id) FROM populations rp) AS count;
            """;

    @Query(value = """
                                      WITH populations AS (SELECT p.population, p.id, p.updated_at
                                                                 FROM population p
                                                                 JOIN server s ON s.id = p.server_id
                                                                 WHERE s.region = ?1 AND p.population > 0
                                                               )
                                                               SELECT
                           """ + statSql, nativeQuery = true)
    StatsProjection findStatsByRegion(int region);

    @Query(value = """
                           WITH populations AS (
                                                      SELECT p.population, p.id, p.updated_at
                                                      FROM population p
                                                      WHERE p.updated_at >= ?1 AND updated_at <= ?2 AND p.population > 0
                                                    )
                                                    SELECT
                           """ + statSql, nativeQuery = true)
    StatsProjection findStatsForAll(LocalDateTime start, LocalDateTime end);

    @Query(value = """
                           WITH populations AS (
                                                      SELECT p.population, p.id, p.updated_at
                                                      FROM population p
                                                      WHERE p.server_id = ?1 AND p.population > 0
                                                    )
                                                    SELECT
                                                      
                           """ + statSql, nativeQuery = true)
    StatsProjection findStatsByServerId(int serverId);

    @Query(value = """
                           WITH populations AS (
                                                      SELECT p.population, p.id, p.updated_at
                                                      FROM population p
                                                      JOIN server s ON s.id = p.server_id
                                                      WHERE s.unique_name = ?1 AND p.population > 0
                                                    )
                                                  
                           """ + statSql, nativeQuery = true)
    StatsProjection findStatsByServerUniqueName(String uniqueName);

    @Query(value = """
                           WITH populations AS (
                                                      SELECT p.population, p.id, p.updated_at
                                                      FROM population p
                                                      JOIN server s ON s.id = p.server_id
                                                      WHERE s.faction = ?1 AND p.population > 0
                                                    )
                                                    SELECT
                           """ + statSql, nativeQuery = true)
    StatsProjection findStatsByFaction(int faction);
}
