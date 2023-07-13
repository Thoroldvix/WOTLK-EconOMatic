package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.shared.SearchRequest;
import com.thoroldvix.economatic.shared.TimeRange;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface PopulationService {

    PopulationPageResponse getAll(TimeRange timeRange, Pageable pageable);

    PopulationPageResponse getForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable);

    TotalPopResponse getTotalPopulation(String serverName);

    PopulationPageResponse search(@Valid SearchRequest searchRequest, Pageable pageable);

    PopulationListResponse getRecentForRegion(String regionName);

    PopulationListResponse getRecentForFaction(String factionName);

    PopulationListResponse getAllRecent();

    PopulationResponse getRecentForServer(String serverIdentifier);

    void saveAll(List<Population> populations);
}
