package com.thoroldvix.economatic.deal.repository;

import com.thoroldvix.economatic.itemprice.model.ItemPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ItemDealsRepository extends JpaRepository<ItemPrice, Long> {


    @Query(value = """
                select i.id as itemId,
                s.unique_name as uniqueServerName,
                        i.name as itemName,
                        ip.market_value as marketValue,
                        ip.min_buyout as minBuyout,
                        (ip.market_value - ip.min_buyout) as dealDiff,
                        cast((cast((ip.market_value - ip.min_buyout) as numeric) / ip.market_value) as decimal(5, 2)) * 100 as discountPercentage
                        from item_price ip
                        join item i on ip.item_id = i.id
                        join server s on s.id = ip.server_id
                        where s.id = ?1 and ip.quantity >= ?2
                        and i.quality >= ?3
                        and ip.market_value > 0
                        and ip.min_buyout < ip.market_value
                        order by discountPercentage desc
                        limit ?4
            """, nativeQuery = true)
    List<ItemDealProjection> findDealsForServer(int serverId, int minQuantity, int minQuality, int limit);
}
