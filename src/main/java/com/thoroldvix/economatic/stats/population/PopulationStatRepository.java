package com.thoroldvix.economatic.stats.population;

import com.thoroldvix.economatic.population.Population;
import com.thoroldvix.economatic.stats.StatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PopulationStatRepository extends JpaRepository<Population, Long> {

    String STAT_SQL = """
            SELECT
            (SELECT AVG(fp.value) FROM filteredPopulations fp) AS mean,
                                       (SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY fp.value) FROM filteredPopulations fp) AS median,
                                       (SELECT fp.id FROM filteredPopulations fp WHERE fp.value = (SELECT MIN(fp1.value) FROM filteredPopulations fp1) ORDER BY fp.updated_at DESC LIMIT 1) AS minId,
                                       (SELECT fp.id FROM filteredPopulations fp WHERE fp.value = (SELECT MAX(fp1.value) FROM filteredPopulations fp1) ORDER BY fp.updated_at DESC LIMIT 1) AS maxId,
                                       (SELECT COUNT(fp.id) FROM filteredPopulations fp) AS count;
            """;

     @Query(value = """
            WITH filteredPopulations AS (SELECT p.value, p.id, p.updated_at
            FROM population p
            JOIN server s ON s.id = p.server_id
            WHERE s.region = ?1 AND p.updated_at >= ?2 and p.updated_at <= ?3 and p.value > 0)
            """ + STAT_SQL, nativeQuery = true)
     StatsProjection findStatsByRegion(int region, LocalDateTime start, LocalDateTime end);

    @Query(value = """
            WITH filteredPopulations AS (
            SELECT p.value, p.id, p.updated_at
            FROM population p
            WHERE p.updated_at >= ?1 AND updated_at <= ?2 AND p.value > 0)
            """ + STAT_SQL, nativeQuery = true)
    StatsProjection findStatsForAll(LocalDateTime start, LocalDateTime end);

    @Query(value = """
            WITH filteredPopulations AS (
            SELECT p.value, p.id, p.updated_at
            FROM population p
            WHERE p.server_id = ?1 AND p.updated_at >= ?2 and p.updated_at <= ?3 and p.value > 0)
            """ + STAT_SQL, nativeQuery = true)
    StatsProjection findStatsByServer(int serverId, LocalDateTime start, LocalDateTime end);

    @Query(value = """
            WITH filteredPopulations AS (
            SELECT p.value, p.id, p.updated_at
            FROM population p
            JOIN server s ON s.id = p.server_id
            WHERE s.faction = ?1 AND p.updated_at >= ?2 and p.updated_at <= ?3 and p.value > 0)
            """ + STAT_SQL, nativeQuery = true)
    StatsProjection findStatsByFaction(int faction, LocalDateTime start, LocalDateTime end);


}
