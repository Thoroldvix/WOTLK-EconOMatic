package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.Faction;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.shared.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.thoroldvix.pricepal.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopulationStatsService implements StatsService<PopulationResponse> {

    private final PopulationMapper populationMapper;
    private final PopulationStatRepository populationStatRepository;
    private final PopulationRepository populationRepository;

    @Override
    public StatsResponse<PopulationResponse> getForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        StatsProjection statsProjection = findForServer(serverIdentifier);
        return getStatResponse(statsProjection, serverIdentifier);
    }

    @Override
    public StatsResponse<PopulationResponse> getForRegion(String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        StatsProjection statsProjection = populationStatRepository.findStatsByRegion(region.ordinal());
        return getStatResponse(statsProjection, regionName);
    }

    @Override
    public StatsResponse<PopulationResponse> getForFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        StatsProjection statsProjection = populationStatRepository.findStatsByFaction(faction.ordinal());
        return getStatResponse(statsProjection, factionName);
    }

    @Override
    public StatsResponse<PopulationResponse> getForAll(int timeRangeInDays) {
        LocalDateTime start = LocalDateTime.now().minusDays(timeRangeInDays);
        LocalDateTime end = LocalDateTime.now();
        StatsProjection statsProjection = populationStatRepository.findStatsForAll(start, end);
        return getStatResponse(statsProjection, "all");
    }

    private StatsResponse<PopulationResponse> getStatResponse(StatsProjection statsProjection, String property) {
        PopulationResponse min = getMin(statsProjection, property);
        PopulationResponse max = getMax(statsProjection, property);

        return StatsResponse.<PopulationResponse>builder()
                .mean(statsProjection.getMean().intValue())
                .median(statsProjection.getMedian().intValue())
                .count(statsProjection.getCount())
                .minimum(min)
                .maximum(max)
                .build();
    }

    private PopulationResponse getMax(StatsProjection statsProjection, String property) {
        return populationRepository.findById(statsProjection.getMaxId().longValue())
                .map(populationMapper::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No max population found for " + property));
    }

    private PopulationResponse getMin(StatsProjection statsProjection, String property) {
        return populationRepository.findById(statsProjection.getMinId().longValue())
                .map(populationMapper::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No min population found for " + property));
    }

    private StatsProjection findForServer(String serverIdentifier) {
        try {
            int serverId = Integer.parseInt(serverIdentifier);
            return populationStatRepository.findStatsByServerId(serverId);
        } catch (NumberFormatException e) {
            return populationStatRepository.findStatsByServerUniqueName(serverIdentifier);
        }
    }
}
