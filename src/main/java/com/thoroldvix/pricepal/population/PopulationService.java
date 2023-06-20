package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.Faction;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.server.ServerService;
import com.thoroldvix.pricepal.shared.SearchRequest;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import com.thoroldvix.pricepal.shared.StringEnumConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.thoroldvix.pricepal.shared.ValidationUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopulationService {

    private final PopulationRepository populationRepository;
    private final ServerService serverService;
    private final PopulationMapper populationMapper;
    private final SearchSpecification<Population> searchSpecification;


    public List<PopulationResponse> getAll(int timeRange, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        LocalDateTime start = LocalDateTime.now().minusDays(timeRange);
        LocalDateTime end = LocalDateTime.now();
        List<Population> populations = populationRepository.findAllForTimeRange(start, end, pageable).getContent();

        validateCollectionNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found"));

        return populationMapper.toResponseList(populations);
    }

    public List<PopulationResponse> getForServer(String serverIdentifier,
                                                 Pageable pageable) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");

        int serverId = serverService.getServer(serverIdentifier).id();
        List<Population> populations = populationRepository.findAllForServer(serverId, pageable).getContent();

        validateCollectionNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found for server identifier: " + serverIdentifier));

        return populationMapper.toResponseList(populations);
    }

    @Transactional
    public void saveAll(List<Population> populations) {
        validateCollectionNotNullOrEmpty(populations,
                () -> new IllegalArgumentException("Population list cannot be empty"));

        populationRepository.saveAll(populations);
    }

    public TotalPopResponse getTotalPopulation(String serverName) {
        validateStringNonNullOrEmpty(serverName, "Server itemName cannot be null or empty");

        TotalPopProjection totalPopProjection = populationRepository.findTotalPopulationForServerName(serverName)
                .orElseThrow(() -> new PopulationNotFoundException("No population found for server itemName " + serverName));

        return TotalPopResponse.builder()
                .popAlliance(totalPopProjection.getPopAlliance())
                .popHorde(totalPopProjection.getPopHorde())
                .popTotal(totalPopProjection.getPopTotal())
                .name(totalPopProjection.getServerName())
                .build();
    }

    public List<PopulationResponse> search(SearchRequest searchRequest, Pageable pageable) {
        Objects.requireNonNull(searchRequest, "Search request cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        Specification<Population> spec = searchSpecification.create(searchRequest.globalOperator(),
                searchRequest.searchCriteria());
        List<Population> populations = populationRepository.findAll(spec, pageable).getContent();

        validateCollectionNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found for search request"));

        return populationMapper.toResponseList(populations);
    }

    public List<PopulationResponse> getAllRecent(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        List<Population> populations = populationRepository.findAllRecent(pageable).getContent();

        validateCollectionNotNullOrEmpty(populations,
                () -> new PopulationNotFoundException("No populations found"));

        return populationMapper.toResponseList(populations);
    }

    public PopulationResponse getRecentForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");

        int serverId = serverService.getServer(serverIdentifier).id();
        Optional<Population> population = populationRepository.findRecentForServer(serverId);

        return population.map(populationMapper::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No recent population found for server identifier: " + serverIdentifier));
    }

    public List<PopulationResponse> getRecentForRegion(String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        List<Population> population = populationRepository.findRecentForRegion(region);

        validateCollectionNotNullOrEmpty(population, () -> new PopulationNotFoundException("No recent populations found for region: " + regionName));

        return populationMapper.toResponseList(population);
    }

    public List<PopulationResponse> getRecentForFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        List<Population> population = populationRepository.findRecentForFaction(faction);

        validateCollectionNotNullOrEmpty(population, () -> new PopulationNotFoundException("No recent populations found for faction: " + factionName));

        return populationMapper.toResponseList(population);
    }


}

