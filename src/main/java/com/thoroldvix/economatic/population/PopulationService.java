package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.shared.SearchRequest;
import com.thoroldvix.economatic.shared.SearchSpecification;
import com.thoroldvix.economatic.shared.StringEnumConverter;
import com.thoroldvix.economatic.shared.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.thoroldvix.economatic.server.ServerErrorMessages.SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY;
import static com.thoroldvix.economatic.shared.ErrorMessages.*;
import static com.thoroldvix.economatic.shared.ValidationUtils.validateCollectionNotNullOrEmpty;
import static com.thoroldvix.economatic.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopulationService {

    public static final String NO_POPULATIONS_FOUND = "No populations found";
    private final PopulationRepository populationRepository;
    private final ServerService serverService;
    private final PopulationMapper populationMapper;
    private final SearchSpecification<Population> searchSpecification;


    public PopulationsPagedResponse getAll(TimeRange timeRange, Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        Page<Population> page = findForTimeRange(timeRange, pageable);
        validateCollectionNotNullOrEmpty(page.getContent(),
                () -> new PopulationNotFoundException(NO_POPULATIONS_FOUND));

        return populationMapper.toPagedResponse(page);
    }

    public PopulationsPagedResponse getForServer(String serverIdentifier,
                                                 TimeRange timeRange,
                                                 Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        Objects.requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        Page<Population> page = findAllForServer(serverIdentifier, timeRange, pageable);
        validateCollectionNotNullOrEmpty(page.getContent(),
                () -> new PopulationNotFoundException("No populations found for server identifier: " + serverIdentifier));

        return populationMapper.toPagedWithServer(page);
    }

    @Transactional
    public void saveAll(List<Population> populations) {
        validateCollectionNotNullOrEmpty(populations,
                () -> new IllegalArgumentException("Population list cannot be empty"));

        populationRepository.saveAll(populations);
    }

    public TotalPopResponse getTotalPopulation(String serverName) {
        validateStringNonNullOrEmpty(serverName, "Server name cannot be null or empty");
        TotalPopProjection totalPopProjection = populationRepository.findTotalPopForServer(serverName)
                .orElseThrow(() -> new PopulationNotFoundException("No total population found for server name " + serverName));
        validateTotalPopProj(totalPopProjection, serverName);
        return populationMapper.toTotalPopResponse(totalPopProjection);
    }

    private void validateTotalPopProj(TotalPopProjection totalPopProjection, String serverName) {
        boolean isInvalid = totalPopProjection.getPopTotal() == null
                || totalPopProjection.getPopHorde() == null
                || totalPopProjection.getPopAlliance() == null;
        if (isInvalid) {
            throw new PopulationNotFoundException("No total population found for server name " + serverName);
        }
    }

    public PopulationsPagedResponse search(SearchRequest searchRequest, Pageable pageable) {
        Objects.requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL);
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        Page<Population> populations = findAllForSearch(searchRequest, pageable);
        validateCollectionNotNullOrEmpty(populations.getContent(),
                () -> new PopulationNotFoundException("No populations found for search request"));

        return populationMapper.toPagedResponse(populations);
    }

    public PopulationsPagedResponse getAllRecent(Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        Page<Population> populations = populationRepository.findAllRecent(pageable);
        validateCollectionNotNullOrEmpty(populations.getContent(),
                () -> new PopulationNotFoundException(NO_POPULATIONS_FOUND));

        return populationMapper.toPagedResponse(populations);
    }

    public PopulationsResponse getRecentForRegion(String regionName) {
        validateStringNonNullOrEmpty(regionName, "Region name cannot be null or empty");
        List<Population> population = findRecentForRegion(regionName);
        validateCollectionNotNullOrEmpty(population,
                () -> new PopulationNotFoundException("No recent populations found for region: " + regionName));

        return populationMapper.toResponseWithRegion(population);
    }

    public PopulationsResponse getRecentForFaction(String factionName) {
        validateStringNonNullOrEmpty(factionName, "Faction name cannot be null or empty");
        List<Population> population = findRecentForFaction(factionName);
        validateCollectionNotNullOrEmpty(population, () -> new PopulationNotFoundException("No recent populations found for faction: " + factionName));

        return populationMapper.toResponseWithFaction(population);
    }

       public PopulationResponse getRecentForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        Population population = findRecentForServer(serverIdentifier);

        return populationMapper.toResponseWithServer(population);
    }

    private Population findRecentForServer(String serverIdentifier) {
        int serverId = serverService.getServer(serverIdentifier).id();
        return populationRepository.findRecentForServer(serverId)
                .orElseThrow(() -> new PopulationNotFoundException("No recent populations found for server: " + serverIdentifier));
    }


    private Page<Population> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<Population> spec = searchSpecification.create(searchRequest.globalOperator(),
                searchRequest.searchCriteria());

        return populationRepository.findAll(spec, pageable);
    }

    private List<Population> findRecentForRegion(String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return populationRepository.findRecentForRegion(region);
    }

    private List<Population> findRecentForFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return populationRepository.findRecentForFaction(faction);
    }

    private Page<Population> findAllForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable) {
        int serverId = serverService.getServer(serverIdentifier).id();
        return populationRepository.findAllForServer(serverId, timeRange.start(), timeRange.end(), pageable);
    }

    private Page<Population> findForTimeRange(TimeRange timeRange, Pageable pageable) {
        return populationRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageable);
    }


}

