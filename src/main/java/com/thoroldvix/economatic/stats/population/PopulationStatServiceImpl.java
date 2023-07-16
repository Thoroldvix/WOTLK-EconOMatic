package com.thoroldvix.economatic.stats.population;

import com.thoroldvix.economatic.dto.TimeRange;
import com.thoroldvix.economatic.error.StatisticsNotFoundException;
import com.thoroldvix.economatic.population.PopulationResponse;
import com.thoroldvix.economatic.population.PopulationService;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.stats.StatsProjection;
import com.thoroldvix.economatic.util.StringEnumConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.thoroldvix.economatic.error.ErrorMessages.TIME_RANGE_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.server.ServerErrorMessages.*;
import static com.thoroldvix.economatic.util.ValidationUtils.notEmpty;
import static java.util.Objects.requireNonNull;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopulationStatServiceImpl implements PopulationStatService {

    private final PopulationStatRepository statRepository;
    private final ServerService serverService;
    private final PopulationStatMapper populationStatMapper;
    private final PopulationService populationServiceImpl;


    @Override
    public PopulationStatResponse getForServer(String serverIdentifier, TimeRange timeRange) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        ServerResponse server = serverService.getServer(serverIdentifier);
        StatsProjection statsProjection = findForServer(server, timeRange);
        validateStatsProjection(statsProjection);

        return getStatResponse(statsProjection);
    }

    @Override
    public PopulationStatResponse getForRegion(String regionName, TimeRange timeRange) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        StatsProjection statsProjection = findForRegion(regionName, timeRange);
        validateStatsProjection(statsProjection);

        return getStatResponse(statsProjection);
    }

    @Override
    public PopulationStatResponse getForFaction(String factionName, TimeRange timeRange) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        StatsProjection statsProjection = findForFaction(factionName, timeRange);
        validateStatsProjection(statsProjection);

        return getStatResponse(statsProjection);
    }

    @Override
    public PopulationStatResponse getForAll(TimeRange timeRange) {
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        StatsProjection statsProjection = findForTimeRange(timeRange);
        validateStatsProjection(statsProjection);

        return getStatResponse(statsProjection);
    }

    private StatsProjection findForTimeRange(TimeRange timeRange) {
        return statRepository.findForTimeRange(timeRange.start(), timeRange.end());
    }

    private StatsProjection findForServer(ServerResponse server, TimeRange timeRange) {
        return statRepository.findStatsByServer(server.id(), timeRange.start(), timeRange.end());
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
        return populationServiceImpl.getForId(maxId);
    }

    private PopulationResponse getMin(StatsProjection statProj) {
        long minId = statProj.getMinId().longValue();
        return populationServiceImpl.getForId(minId);
    }

    private PopulationStatResponse getStatResponse(StatsProjection statsProjection) {
        PopulationResponse min = getMin(statsProjection);
        PopulationResponse max = getMax(statsProjection);

        return populationStatMapper.toResponse(statsProjection, min, max);
    }

    private void validateStatsProjection(StatsProjection statsProjection) {
        boolean isInvalid = statsProjection.getMean() == null
                            || statsProjection.getMaxId() == null
                            || statsProjection.getMinId() == null
                            || statsProjection.getMedian() == null;
        if (isInvalid) {
            throw new StatisticsNotFoundException("No statistics found");
        }
    }
}
