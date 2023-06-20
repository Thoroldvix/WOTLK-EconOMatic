package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.Faction;
import com.thoroldvix.pricepal.server.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PopulationRepository extends JpaRepository<Population, Long>, JpaSpecificationExecutor<Population> {

    @Query(value = """
            WITH populations AS (
              SELECT
                (SELECT p.value
                 FROM population p JOIN server s ON s.id = p.server_id
                 WHERE LOWER(s.name) = LOWER(?1) AND s.faction = 1
                 ORDER BY p.updated_at DESC LIMIT 1) AS popHorde,
                (SELECT p.value
                 FROM population p JOIN server s ON s.id = p.server_id
                 WHERE LOWER(s.name) = LOWER(?1) AND s.faction = 0
                 ORDER BY p.updated_at DESC LIMIT 1) AS popAlliance,
                 (SELECT s.name
                 FROM population p JOIN server s ON s.id = p.server_id
                 WHERE LOWER(s.name) = LOWER(?1)
                 ORDER BY p.updated_at DESC LIMIT 1) AS serverName
            )
            SELECT
              popHorde,
              popAlliance,
              popHorde + popAlliance AS popTotal,
              serverName
            FROM populations""", nativeQuery = true)
    Optional<TotalPopProjection> findTotalPopForServer(String serverName);

    @Query(value = """
            SELECT  p.*
            FROM population p
            INNER JOIN (
                SELECT server_id, MAX(updated_at) AS max_updated_at
                FROM population
                GROUP BY server_id
            ) AS latest_prices ON p.server_id = latest_prices.server_id
            AND p.updated_at = latest_prices.max_updated_at
            """, nativeQuery = true)
    Page<Population> findAllRecent(Pageable pageable);

    @Query(value = """
            select p
            from Population p
            where p.server.id = ?1
            order by p.updatedAt desc
            limit 1
            """)
    Optional<Population> findRecentForServer(int serverId);

    @Query("""
            SELECT p
            FROM Population p
            WHERE p.server.region = ?1
              AND p.updatedAt = (
                SELECT MAX(p2.updatedAt)
                FROM Population p2
                WHERE p2.server.region = ?1
              )
            """)
    List<Population> findRecentForRegion(Region region);

    @Query("""
            SELECT p
            FROM Population p
            WHERE p.server.faction = ?1
              AND p.updatedAt = (
                SELECT MAX(p2.updatedAt)
                FROM Population p2
                WHERE p2.server.faction = ?1
              )
            """)
    List<Population> findRecentForFaction(Faction faction);

    @Query("""
                        SELECT p
                        from Population p
                        join fetch Server s on p.server.id = s.id
                        where s.id = ?1 and p.updatedAt >= ?2 and p.updatedAt <= ?3
            """)
    Page<Population> findAllForServer(int serverId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("""
       select p
       from Population p
       join fetch Server s on p.server.id = s.id
       where p.updatedAt >= ?1 and p.updatedAt <= ?2
    """)
    Page<Population> findAllForTimeRange(LocalDateTime start, LocalDateTime end, Pageable pageable);
}

