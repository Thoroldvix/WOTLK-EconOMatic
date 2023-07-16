package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.common.dto.TimeRange;
import com.thoroldvix.economatic.search.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface ItemPriceService {
    ItemPricePageResponse getRecentForServer(String serverIdentifier, Pageable pageable);

    ItemPriceListResponse getRecentForRegion(String regionName, String itemIdentifier);

    ItemPriceListResponse getRecentForFaction(String factionName, String itemIdentifier);

    ItemPriceListResponse getRecentForServer(String serverIdentifier, String itemIdentifier);

    ItemPricePageResponse search(@Valid SearchRequest searchRequest, Pageable pageable);

    ItemPricePageResponse getForServer(String serverIdentifier, String itemIdentifier, TimeRange timeRange, Pageable pageable);

    ItemPricePageResponse getRecentForItemListAndServers(@Valid ItemPriceRequest request, Pageable pageable);

    void saveAll(List<ItemPrice> itemPricesToSave);
}
