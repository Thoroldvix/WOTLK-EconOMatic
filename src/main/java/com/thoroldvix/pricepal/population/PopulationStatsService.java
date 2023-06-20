package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.Faction;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.server.ServerService;
import com.thoroldvix.pricepal.shared.StatsProjection;
import com.thoroldvix.pricepal.shared.StringEnumConverter;
import com.thoroldvix.pricepal.shared.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.thoroldvix.pricepal.server.ServerErrorMessages.*;
import static com.thoroldvix.pricepal.shared.ErrorMessages.TIME_RANGE_CANNOT_BE_NULL;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopulationStatsService {

    private final PopulationStatRepository populationStatRepository;
    private final ServerService serverServiceImpl;
    private final PopulationStatMapper populationStatMapper;


    public PopulationStatResponse getForServer(String serverIdentifier, TimeRange timeRange) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        Objects.requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);
        StatsProjection statsProjection = findForServer(serverIdentifier, timeRange);
        return populationStatMapper.toResponse(statsProjection, populationStatRepository);
    }

    public PopulationStatResponse getForRegion(String regionName, TimeRange timeRange) {
        validateStringNonNullOrEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        Objects.requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);
        StatsProjection statsProjection = findForRegion(regionName, timeRange);
        return populationStatMapper.toResponse(statsProjection, populationStatRepository);
    }


    public PopulationStatResponse getForFaction(String factionName, TimeRange timeRange) {
        validateStringNonNullOrEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        Objects.requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);
        StatsProjection statsProjection = findForFaction(factionName, timeRange);
        return populationStatMapper.toResponse(statsProjection, populationStatRepository);
    }

    public PopulationStatResponse getForAll(TimeRange timeRange) {
        StatsProjection statsProjection = populationStatRepository.findStatsForAll(timeRange.start(), timeRange.end());
        return populationStatMapper.toResponse(statsProjection, populationStatRepository);
    }


    private StatsProjection findForServer(String serverIdentifier, TimeRange timeRange) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        Objects.requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return populationStatRepository.findStatsByServer(serverId, timeRange.start(), timeRange.end());
    }


    private StatsProjection findForFaction(String factionName, TimeRange timeRange) {
        validateStringNonNullOrEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        Objects.requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return populationStatRepository.findStatsByFaction(faction.ordinal(), timeRange.start(), timeRange.end());
    }

    private StatsProjection findForRegion(String regionName, TimeRange timeRange) {
        validateStringNonNullOrEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        Objects.requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return populationStatRepository.findStatsByRegion(region.ordinal(), timeRange.start(), timeRange.end());
    }
}
