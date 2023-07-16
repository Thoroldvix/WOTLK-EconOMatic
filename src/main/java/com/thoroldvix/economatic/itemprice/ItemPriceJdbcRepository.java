package com.thoroldvix.economatic.itemprice;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
class ItemPriceJdbcRepository {

    private static final int BATCH_SIZE = 100;
    private final JdbcTemplate jdbcTemplate;

    public void saveAll(Collection<ItemPrice> prices) {
        jdbcTemplate.batchUpdate("""
                INSERT INTO item_price (min_buyout, historical_value, market_value, quantity, num_auctions, item_id, server_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                 """, prices, BATCH_SIZE, (ps, price) -> {
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
