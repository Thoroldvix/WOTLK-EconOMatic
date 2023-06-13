package com.thoroldvix.pricepal.goldprice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GoldPriceRepository extends JpaRepository<GoldPrice, Long>, JpaSpecificationExecutor<GoldPrice> {

    @Query("select sp from GoldPrice sp where sp.price = ?1 order by sp.updatedAt desc limit 1")
    Optional<GoldPrice> findByPrice(BigDecimal price);

    @Query(value = """
            SELECT  sp.*
            FROM server_price sp
            INNER JOIN (
                SELECT server_id, MAX(updated_at) AS max_updated_at
                FROM server_price
                GROUP BY server_id
            ) AS latest_prices ON sp.server_id = latest_prices.server_id
            AND sp.updated_at = latest_prices.max_updated_at
            """, nativeQuery = true)
    Page<GoldPrice> findAllRecent(Pageable pageable);

    @Query("select sp from GoldPrice sp where sp.updatedAt >= ?1 and sp.updatedAt <= ?2")
    Page<GoldPrice> findAllForTimeRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query(value = """
            select sp
            from GoldPrice sp where sp.server.id = ?1
            and sp.updatedAt = (select max(sp2.updatedAt) from GoldPrice sp2 where sp2.server.id = ?1)
            """)
    Optional<GoldPrice> findRecentByServerId(int serverId);

    @Query(value = """
            select sp
            from GoldPrice sp where sp.server.uniqueName = ?1
            and sp.updatedAt = (select max(sp2.updatedAt) from GoldPrice sp2 where sp2.server.uniqueName = ?1)
            """)
    Optional<GoldPrice> findRecentByServerUniqueName(String uniqueServerName);
}