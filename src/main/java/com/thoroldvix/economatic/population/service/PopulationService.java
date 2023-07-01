package com.thoroldvix.economatic.population.service;

import com.thoroldvix.economatic.population.model.Population;
import com.thoroldvix.economatic.population.dto.PopulationResponse;
import com.thoroldvix.economatic.population.dto.PopulationPageResponse;
import com.thoroldvix.economatic.population.dto.PopulationListResponse;
import com.thoroldvix.economatic.population.dto.TotalPopResponse;
import com.thoroldvix.economatic.population.error.PopulationNotFoundException;
import com.thoroldvix.economatic.population.mapper.PopulationMapper;
import com.thoroldvix.economatic.population.repository.PopulationRepository;
import com.thoroldvix.economatic.population.repository.TotalPopProjection;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.service.ServerService;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import com.thoroldvix.economatic.shared.service.SearchSpecification;
import com.thoroldvix.economatic.shared.util.StringEnumConverter;
import com.thoroldvix.economatic.shared.dto.TimeRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.thoroldvix.economatic.server.error.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.error.ErrorMessages.*;
import static com.thoroldvix.economatic.shared.util.Utils.notEmpty;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopulationService {

    public static final String NO_POPULATIONS_FOUND = "No populations found";
    private final PopulationRepository populationRepository;
    private final ServerService serverService;
    private final PopulationMapper populationMapper;
    private final SearchSpecification<Population> searchSpecification;

    @Cacheable("population-cache")
    public PopulationPageResponse getAll(
            @Valid @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange,
            @NotNull(message = PAGE_CANNOT_BE_NULL)
            Pageable pageable) {
        Page<Population> page = findForTimeRange(timeRange, pageable);
        notEmpty(page.getContent(),
                () -> new PopulationNotFoundException(NO_POPULATIONS_FOUND));

        return populationMapper.toPageResponse(page);
    }

    @Cacheable("population-cache")
    public PopulationPageResponse getForServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier,
            @Valid @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange,
            @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
            Pageable pageable) {

        Page<Population> page = findAllForServer(serverIdentifier, timeRange, pageable);
        notEmpty(page.getContent(),
                () -> new PopulationNotFoundException("No populations found for server identifier: " + serverIdentifier));

        return populationMapper.toPageResponse(page);
    }

    @Transactional
    public void saveAll(
            @NotEmpty(message = "Population list cannot be null or empty")
            List<Population> populations) {
        populationRepository.saveAll(populations);
    }

    public TotalPopResponse getTotalPopulation(
            @NotEmpty(message = "Server name cannot be null or empty")
            String serverName) {
        TotalPopProjection totalPopProjection = populationRepository.findTotalPopForServer(serverName)
                .orElseThrow(() -> new PopulationNotFoundException("No total population found for server name " + serverName));
        validateTotalPopProj(totalPopProjection, serverName);
        return populationMapper.toTotalPopResponse(totalPopProjection);
    }

    @Cacheable("population-cache")
    public PopulationPageResponse search(
            @Valid @NotNull(message = SEARCH_REQUEST_CANNOT_BE_NULL)
            SearchRequest searchRequest,
            @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
            Pageable pageable) {
        Page<Population> populations = findAllForSearch(searchRequest, pageable);
        notEmpty(populations.getContent(),
                () -> new PopulationNotFoundException("No populations found for search request"));

        return populationMapper.toPageResponse(populations);
    }

    public PopulationListResponse getAllRecent() {
        List<Population> populations = populationRepository.findAllRecent();
        notEmpty(populations,
                () -> new PopulationNotFoundException(NO_POPULATIONS_FOUND));

        return populationMapper.toPopulationList(populations);
    }

    public PopulationListResponse getRecentForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName) {
        List<Population> population = findRecentForRegion(regionName);
        notEmpty(population,
                () -> new PopulationNotFoundException("No recent populations found for region: " + regionName));

        return populationMapper.toPopulationList(population);
    }

    public PopulationListResponse getRecentForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName) {
        List<Population> population = findRecentForFaction(factionName);
        notEmpty(population, () -> new PopulationNotFoundException("No recent populations found for faction: " + factionName));

        return populationMapper.toPopulationList(population);
    }

    public PopulationResponse getRecentForServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier) {
        Population population = findRecentForServer(serverIdentifier);

        return populationMapper.toResponse(population);
    }

    private void validateTotalPopProj(TotalPopProjection totalPopProjection, String serverName) {
        boolean isInvalid = totalPopProjection.getPopTotal() == null
                || totalPopProjection.getPopHorde() == null
                || totalPopProjection.getPopAlliance() == null;
        if (isInvalid) {
            throw new PopulationNotFoundException("No total population found for server name " + serverName);
        }
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

