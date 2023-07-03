package com.thoroldvix.economatic.population.service;

import com.thoroldvix.economatic.population.dto.PopulationResponse;
import com.thoroldvix.economatic.population.dto.PopulationStatResponse;
import com.thoroldvix.economatic.population.error.PopulationNotFoundException;
import com.thoroldvix.economatic.population.mapper.PopulationMapper;
import com.thoroldvix.economatic.population.mapper.PopulationStatMapper;
import com.thoroldvix.economatic.population.repository.PopulationStatRepository;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.service.ServerService;
import com.thoroldvix.economatic.shared.StatsProjection;
import com.thoroldvix.economatic.shared.dto.TimeRange;
import com.thoroldvix.economatic.shared.util.StringEnumConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static com.thoroldvix.economatic.error.ErrorMessages.TIME_RANGE_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.server.error.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.util.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.shared.util.ValidationUtils.validateStatsProjection;
import static java.util.Objects.requireNonNull;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopulationStatsService {

    private final PopulationStatRepository statRepository;
    private final ServerService serverService;
    private final PopulationStatMapper populationStatMapper;
    private final PopulationMapper populationMapper;


    public PopulationStatResponse getForServer(String serverIdentifier, TimeRange timeRange) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        return generatePopulationStatResponse(() -> findForServer(serverIdentifier, timeRange));
    }

    public PopulationStatResponse getForRegion(String regionName, TimeRange timeRange) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        return generatePopulationStatResponse(() -> findForRegion(regionName, timeRange));
    }


    public PopulationStatResponse getForFaction(String factionName, TimeRange timeRange) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        return generatePopulationStatResponse(() -> findForFaction(factionName, timeRange));
    }

    public PopulationStatResponse getForAll(TimeRange timeRange) {
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        return generatePopulationStatResponse(() ->
                statRepository.findStatsForAll(timeRange.start(), timeRange.end()));
    }

    private PopulationStatResponse generatePopulationStatResponse(Supplier<StatsProjection> statsSupplier) {
        StatsProjection statsProjection = statsSupplier.get();
        validateStatsProjection(statsProjection);

        return getStatResponse(statsProjection);
    }


    private StatsProjection findForServer(String serverIdentifier, TimeRange timeRange) {
        int serverId = serverService.getServer(serverIdentifier).id();

        return statRepository.findStatsByServer(serverId, timeRange.start(), timeRange.end());
    }


    private StatsProjection findForFaction(String factionName, TimeRange timeRange) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);

        return statRepository.findStatsByFaction(faction.ordinal(), timeRange.start(), timeRange.end());
    }

    private StatsProjection findForRegion(String regionName, TimeRange timeRange) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);

        return statRepository.findStatsByRegion(region.ordinal(), timeRange.start(), timeRange.end());
    }

    private PopulationResponse getMax(StatsProjection statProj) {
        long maxId = statProj.getMaxId().longValue();

        return statRepository.findById(maxId).map(populationMapper::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No max population found for id " + maxId));
    }

    private PopulationResponse getMin(StatsProjection statProj) {
        long minId = statProj.getMinId().longValue();

        return statRepository.findById(minId).map(populationMapper::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No min population found for id " + minId));
    }

    private PopulationStatResponse getStatResponse(StatsProjection statsProjection) {
        PopulationResponse min = getMin(statsProjection);
        PopulationResponse max = getMax(statsProjection);

        return populationStatMapper.toResponse(statsProjection, min, max);
    }
}
