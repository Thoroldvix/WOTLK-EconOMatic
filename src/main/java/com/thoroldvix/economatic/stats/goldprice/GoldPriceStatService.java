package com.thoroldvix.economatic.stats.goldprice;

import com.thoroldvix.economatic.shared.TimeRange;

public interface GoldPriceStatService {

    GoldPriceStatResponse getForServer(String serverIdentifier, TimeRange timeRange);

    GoldPriceStatResponse getForRegion(String regionName, TimeRange timeRange);

    GoldPriceStatResponse getForFaction(String factionName, TimeRange timeRange);

    GoldPriceStatResponse getForAll(TimeRange timeRange);
}
