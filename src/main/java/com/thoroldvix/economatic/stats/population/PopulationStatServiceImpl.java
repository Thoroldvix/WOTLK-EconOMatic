package com.thoroldvix.economatic.stats.population;

import com.thoroldvix.economatic.common.dto.TimeRange;
import com.thoroldvix.economatic.common.util.StringEnumConverter;
import com.thoroldvix.economatic.error.StatisticsNotFoundException;
import com.thoroldvix.economatic.population.PopulationResponse;
import com.thoroldvix.economatic.population.PopulationService;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.stats.StatsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.thoroldvix.economatic.common.util.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.error.ErrorMessages.*;
import static java.util.Objects.requireNonNull;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class PopulationStatServiceImpl implements PopulationStatService {

    private final PopulationStatRepository statRepository;
    private final ServerService serverService;
    private final PopulationStatMapper populationStatMapper;
    private final PopulationService populationServiceImpl;

    @Override
    public PopulationStatResponse getForServer(String serverIdentifier, TimeRange timeRange) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL.message);

        ServerResponse server = serverService.getServer(serverIdentifier);
        StatsProjection statsProjection = statRepository.findStatsByServer(server.id(),
                timeRange.start(),
                timeRange.end()
        );
        validateStatsProjection(statsProjection);
        return getStatResponse(statsProjection);
    }

    private PopulationStatResponse getStatResponse(StatsProjection statsProjection) {
        PopulationResponse min = getMin(statsProjection);
        PopulationResponse max = getMax(statsProjection);

        return populationStatMapper.toResponse(statsProjection, min, max);
    }

    private PopulationResponse getMax(StatsProjection statProj) {
        long maxId = statProj.getMaxId().longValue();
        return populationServiceImpl.getForId(maxId);
    }

    private PopulationResponse getMin(StatsProjection statProj) {
        long minId = statProj.getMinId().longValue();
        return populationServiceImpl.getForId(minId);
    }

    private void validateStatsProjection(StatsProjection statsProjection) {
        boolean isInvalid = statsProjection.getMean() == null
                || statsProjection.getMaxId() == null
                || statsProjection.getMinId() == null
                || statsProjection.getMedian() == null;
        if (isInvalid) {
            throw new StatisticsNotFoundException(NO_STATISTICS_FOUND.message);
        }
    }

    @Override
    public PopulationStatResponse getForRegion(String regionName, TimeRange timeRange) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL.message);

        Region region = StringEnumConverter.fromString(regionName, Region.class);
        StatsProjection statsProjection = statRepository.findStatsByRegion(
                region.ordinal(),
                timeRange.start(),
                timeRange.end()
        );

        validateStatsProjection(statsProjection);
        return getStatResponse(statsProjection);
    }

    @Override
    public PopulationStatResponse getForFaction(String factionName, TimeRange timeRange) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL.message);

        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        StatsProjection statsProjection = statRepository.findStatsByFaction(
                faction.ordinal(),
                timeRange.start(),
                timeRange.end()
        );

        validateStatsProjection(statsProjection);
        return getStatResponse(statsProjection);
    }

    @Override
    public PopulationStatResponse getForAll(TimeRange timeRange) {
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL.message);

        StatsProjection statsProjection = statRepository.findForTimeRange(
                timeRange.start(),
                timeRange.end()
        );
        validateStatsProjection(statsProjection);

        return getStatResponse(statsProjection);
    }

}
