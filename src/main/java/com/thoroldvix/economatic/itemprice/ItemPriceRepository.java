package com.thoroldvix.economatic.itemprice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Repository
interface ItemPriceRepository extends JpaRepository<ItemPrice, Long>, JpaSpecificationExecutor<ItemPrice> {


    @EntityGraph(attributePaths = {"item", "server"})
    @Query(""" 
                select ip
                from ItemPrice ip
                where ip.server.id = ?1
                and ip.updatedAt = (select max(ip2.updatedAt)
                from ItemPrice ip2 where ip2.server.id = ?1)
            """)
    Page<ItemPrice> findRecentForServer(int serverId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "server"})
    @Query("""
            select ip
             from ItemPrice ip
             where ip.server.id = ?1 and ip.item.id = ?2
             and ip.updatedAt = (select max(ip2.updatedAt) from ItemPrice ip2 where ip2.server.id = ?1 and ip.item.id = ?2)
            """)
    List<ItemPrice> findRecentForServerAndItem(int serverId, int itemId);

    @EntityGraph(attributePaths = {"item", "server"})
    @Query("""
            select ip
             from ItemPrice ip
             where ip.server.id = ?1 and ip.item.id = ?2
             and (ip.updatedAt >= ?3 and ip.updatedAt <= ?4)
            """)
    Page<ItemPrice> findForServerAndTimeRange(int serverId, int itemId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT ON (s.id) ip.id, ip.min_buyout, ip.historical_value, ip.market_value, ip.quantity, ip.num_auctions, ip.item_id, ip.server_id, ip.updated_at
            FROM item_price ip
            JOIN item i ON ip.item_id = i.id
            JOIN server s ON s.id = ip.server_id
            WHERE s.region = ?1 and i.id = ?2
            ORDER BY s.id, ip.updated_at DESC;
            """, nativeQuery = true)
    List<ItemPrice> findRecentForRegionAndItem(int region, int itemId);

    @Query(value = """
            SELECT DISTINCT ON (s.id) ip.id, ip.min_buyout, ip.historical_value, ip.market_value, ip.quantity, ip.num_auctions, ip.item_id, ip.server_id, ip.updated_at
            FROM item_price ip
            JOIN item i ON ip.item_id = i.id
            JOIN server s ON s.id = ip.server_id
            WHERE s.faction = ?1 and i.id = ?2
            ORDER BY s.id, ip.updated_at DESC;
            """, nativeQuery = true)
    List<ItemPrice> findRecentForFactionAndItem(int faction, int itemId);

    @Query(value = """
            SELECT ip.*
            FROM item_price ip
                     INNER JOIN (SELECT server_id, MAX(updated_at) AS max_updated_at
                                 FROM item_price
                                 GROUP BY server_id) AS latest_prices ON ip.server_id = latest_prices.server_id
                AND ip.updated_at = latest_prices.max_updated_at
            where item_id in ?1
            """, nativeQuery = true)
    Page<ItemPrice> findRecentForItemList(Set<Integer> itemIds, Pageable pageable);

    @Query(value = """
            SELECT ip.*
            FROM item_price ip
                     INNER JOIN (SELECT server_id, MAX(updated_at) AS max_updated_at
                                 FROM item_price
                                 GROUP BY server_id) AS latest_prices ON ip.server_id = latest_prices.server_id
                AND ip.updated_at = latest_prices.max_updated_at
            where item_id in ?1 and ip.server_id in ?2
            """, nativeQuery = true)
    Page<ItemPrice> findRecentForItemListAndServers(Set<Integer> itemIds, Set<Integer> serverIds, Pageable pageable);

    default void saveAll(Collection<ItemPrice> prices, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.batchUpdate("""
                INSERT INTO item_price (min_buyout, historical_value, market_value, quantity, num_auctions, item_id, server_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                 """, prices, 100, (ps, price) -> {
            ps.setLong(1, price.getMinBuyout());
            ps.setLong(2, price.getHistoricalValue());
            ps.setLong(3, price.getMarketValue());
            ps.setInt(4, price.getQuantity());
            ps.setInt(5, price.getNumAuctions());
            ps.setInt(6, price.getItem().getId());
            ps.setInt(7, price.getServer().getId());
        });
    }
}
