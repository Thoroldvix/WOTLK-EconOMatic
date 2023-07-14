package com.thoroldvix.economatic.stats.population;

import com.thoroldvix.economatic.dto.TimeRange;

public interface PopulationStatService {

    PopulationStatResponse getForServer(String serverIdentifier, TimeRange timeRange);

    PopulationStatResponse getForRegion(String regionName, TimeRange timeRange);

    PopulationStatResponse getForFaction(String factionName, TimeRange timeRange);

    PopulationStatResponse getForAll(TimeRange timeRange);
}
