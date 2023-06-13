package com.thoroldvix.pricepal.population;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PopulationRepository extends JpaRepository<Population, Long>, JpaSpecificationExecutor<Population> {

    @Query("select p from Population p where p.population = ?1 order by p.updatedAt desc limit 1")
    Optional<Population> findByPopulationSize(int populationSize);

    @Query(value = """
            WITH populations AS (
              SELECT
                (SELECT p.population
                 FROM population p JOIN server s ON s.id = p.server_id
                 WHERE LOWER(s.name) = LOWER(?1) AND s.faction = 'HORDE'
                 ORDER BY p.updated_at DESC LIMIT 1) AS hordePop,
                (SELECT p.population
                 FROM population p JOIN server s ON s.id = p.server_id
                 WHERE LOWER(s.name) = LOWER(?1) AND s.faction = 'ALLIANCE'
                 ORDER BY p.updated_at DESC LIMIT 1) AS alliancePop
            )
            SELECT
              hordePop,
              alliancePop,
              hordePop + alliancePop AS totalPop,
              ?1 AS serverName
            FROM populations""", nativeQuery = true)
    Optional<TotalPopProjection> findTotalPopulationForServerName(String serverName);

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
            select sp
            from ServerPrice sp where sp.server.id = ?1
            and sp.updatedAt = (select max(sp2.updatedAt) from ServerPrice sp2 where sp2.server.id = ?1)
            """)
    Optional<Population> findRecentByServerId(int serverId);

    @Query(value = """
            select sp
            from ServerPrice sp where sp.server.uniqueName = ?1
            and sp.updatedAt = (select max(sp2.updatedAt) from ServerPrice sp2 where sp2.server.uniqueName = ?1)
            """)
    Optional<Population> findRecentByServerUniqueName(String uniqueServerName);

    @Query("select p from Population p where p.updatedAt >= ?1 and p.updatedAt <= ?2")
    Page<Population> findAllForTimeRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
}

