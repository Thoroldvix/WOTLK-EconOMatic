package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.common.dto.TimeRange;
import com.thoroldvix.economatic.common.util.StringEnumConverter;
import com.thoroldvix.economatic.search.SearchRequest;
import com.thoroldvix.economatic.search.SpecificationBuilder;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.thoroldvix.economatic.common.util.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.error.ErrorMessages.*;
import static java.util.Objects.requireNonNull;

@Service
@Cacheable("population-cache")
@RequiredArgsConstructor
@Transactional(readOnly = true)
class PopulationServiceImpl implements PopulationService {

    public static final String NO_POPULATIONS_FOUND = "No populations found";

    private final PopulationRepository populationRepository;
    private final ServerService serverService;
    private final PopulationMapper populationMapper;

    @Override
    public PopulationResponse getForId(long id) {
        return populationRepository.findById(id).map(populationMapper::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No population found for id " + id));
    }

    @Override
    public PopulationPageResponse getAll(TimeRange timeRange, Pageable pageable) {
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL.message);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL.message);

        Page<Population> page = populationRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageable);

        notEmpty(page.getContent(),
                () -> new PopulationNotFoundException(NO_POPULATIONS_FOUND));

        return populationMapper.toPageResponse(page);
    }

    @Override
    public PopulationPageResponse getForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL.message);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL.message);

        ServerResponse server = serverService.getServer(serverIdentifier);
        Page<Population> page = populationRepository.findAllForServer(server.id(),
                timeRange.start(),
                timeRange.end(),
                pageable
        );

        notEmpty(page.getContent(),
                () -> new PopulationNotFoundException("No populations found for server identifier: " + serverIdentifier));

        return populationMapper.toPageResponse(page);
    }

    @Override
    public TotalPopResponse getTotalPopulation(String serverName) {
        notEmpty(serverName, "Server name cannot be null or empty");

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

    @Override
    public PopulationPageResponse search(@Valid SearchRequest searchRequest, Pageable pageable) {
        requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL.message);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL.message);

        Specification<Population> spec = SpecificationBuilder.from(searchRequest);
        Page<Population> populations = populationRepository.findAll(spec, pageable);
        notEmpty(populations.getContent(),
                () -> new PopulationNotFoundException("No populations found for search request"));

        return populationMapper.toPageResponse(populations);
    }

    @Override
    public PopulationListResponse getRecentForRegion(String regionName) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);

        Region region = StringEnumConverter.fromString(regionName, Region.class);
        List<Population> population = populationRepository.findRecentForRegion(region);
        notEmpty(population,
                () -> new PopulationNotFoundException("No recent populations found for region: " + regionName));

        return populationMapper.toPopulationList(population);
    }

    @Override
    public PopulationListResponse getRecentForFaction(String factionName) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);

        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        List<Population> population = populationRepository.findRecentForFaction(faction);
        notEmpty(population, () -> new PopulationNotFoundException("No recent populations found for faction: " + factionName));

        return populationMapper.toPopulationList(population);
    }

    @Override
    public PopulationListResponse getAllRecent() {
        List<Population> populations = populationRepository.findAllRecent();
        notEmpty(populations,
                () -> new PopulationNotFoundException(NO_POPULATIONS_FOUND));

        return populationMapper.toPopulationList(populations);
    }

    @Override
    public PopulationResponse getRecentForServer(String serverIdentifier) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        ServerResponse server = serverService.getServer(serverIdentifier);
        Population population = populationRepository.findRecentForServer(server.id())
                .orElseThrow(() -> new PopulationNotFoundException("No recent populations found for server: " + serverIdentifier));

        return populationMapper.toResponse(population);
    }

    @Override
    @Transactional
    public void saveAll(List<Population> populations) {
        notEmpty(populations,
                () -> new IllegalArgumentException("Population list cannot be null or empty"));

        populationRepository.saveAll(populations);
    }
}

