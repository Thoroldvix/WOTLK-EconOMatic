package com.thoroldvix.pricepal.goldprice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
    Page<GoldPrice> findAllRecent(Pageable pageable);

    @Query(value = """
            select gp
            from GoldPrice gp where gp.server.id = ?1
            and gp.updatedAt = (select max(gp2.updatedAt) from GoldPrice gp2 where gp2.server.id = ?1)
            """)
    Optional<GoldPrice> findRecentByServerId(int serverId);

    @Query(value = """
            select gp
            from GoldPrice gp where gp.server.uniqueName = ?1
            and gp.updatedAt = (select max(gp2.updatedAt) from GoldPrice gp2 where gp2.server.uniqueName = ?1)
            """)
    Optional<GoldPrice> findRecentByServerUniqueName(String uniqueName);
}