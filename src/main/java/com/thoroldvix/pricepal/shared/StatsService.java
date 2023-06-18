package com.thoroldvix.pricepal.shared;

public interface StatsService<T> {
    StatsResponse<T> getForServer(String serverIdentifier);

    StatsResponse<T> getForRegion(String regionName);

    StatsResponse<T> getForFaction(String factionName);

    StatsResponse<T> getForAll(int timeRangeInDays);
}
