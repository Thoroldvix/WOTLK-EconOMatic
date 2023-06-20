package com.thoroldvix.pricepal.goldprice;

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
public interface GoldPriceRepository extends JpaRepository<GoldPrice, Long>, JpaSpecificationExecutor<GoldPrice> {

    @Query(value = """
            SELECT  gp.*
            FROM gold_price gp
            INNER JOIN (
                SELECT server_id, MAX(updated_at) AS max_updated_at
                FROM gold_price
                GROUP BY server_id
            ) AS latest_prices ON gp.server_id = latest_prices.server_id
            AND gp.updated_at = latest_prices.max_updated_at
            """, nativeQuery = true)
    List<GoldPrice> findAllRecent();

    @Query("""
            select gp
            from GoldPrice gp
            where gp.server.id = ?1 and gp.updatedAt >= ?2 and gp.updatedAt <= ?3
            """)
    Page<GoldPrice> findAllForServerAndTimeRange(int serverId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("""
            select gp
            from GoldPrice gp
            where gp.updatedAt >= ?1 and gp.updatedAt <= ?2
            """)
    Page<GoldPrice> findAllForTimeRange(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT ON (s.id) gp.id, gp.value, gp.server_id,  gp.updated_at
            FROM gold_price gp
            JOIN server s ON s.id = gp.server_id
            WHERE s.region = ?1
            ORDER BY s.id, gp.updated_at DESC;
            """, nativeQuery = true)
    List<GoldPrice> findRecentForRegion(int region);

    @Query(value = """
            SELECT DISTINCT ON (s.id) gp.id, gp.value, gp.server_id,  gp.updated_at
            FROM gold_price gp
            JOIN server s ON s.id = gp.server_id
            WHERE s.faction = ?1
            ORDER BY s.id, gp.updated_at DESC;
            """, nativeQuery = true)
    List<GoldPrice> findRecentForFaction(int ordinal);

    @Query(value = """
            select gp
            from GoldPrice gp
            where gp.server.id = ?1
            order by gp.updatedAt desc
            limit 1
            """)
    Optional<GoldPrice> findRecentForServer(int serverId);
}