package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.shared.StatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PopulationStatRepository extends JpaRepository<Population, Long> {

    String STAT_SQL = """
            SELECT
            (SELECT AVG(rp.value) FROM filteredPopulations rp) AS mean,
                                       (SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY rp.value) FROM filteredPopulations rp) AS median,
                                       (SELECT rp.id FROM filteredPopulations rp WHERE rp.value = (SELECT MIN(rp1.value) FROM filteredPopulations rp1) ORDER BY rp.updated_at DESC LIMIT 1) AS minId,
                                       (SELECT rp.id FROM filteredPopulations rp WHERE rp.value = (SELECT MAX(rp1.value) FROM filteredPopulations rp1) ORDER BY rp.updated_at DESC LIMIT 1) AS maxId,
                                       (SELECT COUNT(rp.id) FROM filteredPopulations rp) AS count;
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
