package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.shared.StatsProjection;
import com.thoroldvix.economatic.shared.StringEnumConverter;
import com.thoroldvix.economatic.shared.TimeRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.function.Supplier;

import static com.thoroldvix.economatic.server.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.ErrorMessages.TIME_RANGE_CANNOT_BE_NULL;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopulationStatsService {

    private final PopulationStatRepository statRepository;
    private final ServerService serverService;
    private final PopulationStatMapper populationStatMapper;
    private final PopulationMapper populationMapper;


    public PopulationStatResponse getForServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier,
            @Valid @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange) {
         return generatePopulationStatResponse(() -> findForServer(serverIdentifier, timeRange));
    }

    public PopulationStatResponse getForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName,
            @Valid @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange) {
        return generatePopulationStatResponse(() -> findForRegion(regionName, timeRange));
    }


    public PopulationStatResponse getForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName,
            @Valid @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange) {
        return generatePopulationStatResponse(() -> findForFaction(factionName, timeRange));
    }

    public PopulationStatResponse getForAll(
            @Valid @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange) {
        return generatePopulationStatResponse(() ->
                statRepository.findStatsForAll(timeRange.start(), timeRange.end()));
    }

    private PopulationStatResponse generatePopulationStatResponse(Supplier<StatsProjection> statsSupplier) {
        StatsProjection statsProjection = statsSupplier.get();
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
