package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.common.dto.TimeRange;
import com.thoroldvix.economatic.search.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface GoldPriceService {

    GoldPriceResponse getForId(long id);

    GoldPricePageResponse getAll(TimeRange timeRange, Pageable pageable);

    GoldPriceListResponse getAllRecent();

    GoldPricePageResponse search(@Valid SearchRequest searchRequest, Pageable pageable);

    GoldPricePageResponse getForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable);

    GoldPriceResponse getRecentForServer(String serverIdentifier);

    GoldPriceListResponse getRecentForRegion(String regionName);

    GoldPriceListResponse getRecentForFaction(String factionName);

    GoldPriceListResponse getRecentForServerList(@Valid GoldPriceRequest request);

    void saveAll(List<GoldPrice> pricesToSave);
}
