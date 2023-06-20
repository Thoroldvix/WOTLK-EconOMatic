package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.population.PopulationNotFoundException;
import com.thoroldvix.pricepal.server.Faction;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.server.ServerService;
import com.thoroldvix.pricepal.shared.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static com.thoroldvix.pricepal.shared.ValidationUtils.validatePositiveInt;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoldPriceStatsService implements StatsService<GoldPriceResponse> {

    private final ServerService serverServiceImpl;
    private final GoldPriceStatRepository goldPriceStatRepository;
    private final GoldPriceMapper goldPriceMapper;

    @Override
    public StatsResponse<GoldPriceResponse> getForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        StatsProjection statsProjection = findForServer(serverIdentifier);
        return getStatResponse(statsProjection, serverIdentifier);
    }

    @Override
    public StatsResponse<GoldPriceResponse> getForRegion(String regionName) {
        validateStringNonNullOrEmpty(regionName, "Region name cannot be null or empty");
        StatsProjection statsProjection = findForRegion(regionName);
        return getStatResponse(statsProjection, regionName);
    }

    private StatsProjection findForRegion(String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return goldPriceStatRepository.findForRegion(region.ordinal());
    }

    @Override
    public StatsResponse<GoldPriceResponse> getForFaction(String factionName) {
        validateStringNonNullOrEmpty(factionName, "Faction name cannot be null or empty");
        StatsProjection statsProjection = findForFaction(factionName);
        return getStatResponse(statsProjection, factionName);
    }

    private StatsProjection findForFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return goldPriceStatRepository.findForFaction(faction.ordinal());
    }

    @Override
    public StatsResponse<GoldPriceResponse> getForAll(int timeRange) {
        validatePositiveInt(timeRange, "Time range must be a positive integer");
        StatsProjection statsProjection = findForTimeRange(timeRange);
        return getStatResponse(statsProjection, "all");
    }

    private StatsProjection findForTimeRange(int timeRange) {
        LocalDateTime start = LocalDateTime.now().minusDays(timeRange);
        LocalDateTime end = LocalDateTime.now();
        return goldPriceStatRepository.findStatsForAll(start, end);
    }

    private StatsResponse<GoldPriceResponse> getStatResponse(StatsProjection statsProjection, String property) {
        GoldPriceResponse min = getMin(statsProjection, property);
        GoldPriceResponse max = getMax(statsProjection, property);

        BigDecimal mean = BigDecimal.valueOf(statsProjection.getMean().doubleValue())
                .setScale(6, RoundingMode.HALF_UP);
        BigDecimal median = BigDecimal.valueOf(statsProjection.getMedian().doubleValue())
                .setScale(6, RoundingMode.HALF_UP);

        return StatsResponse.<GoldPriceResponse>builder()
                .mean(mean)
                .median(median)
                .count(statsProjection.getCount())
                .minimum(min)
                .maximum(max)
                .build();
    }

    private GoldPriceResponse getMax(StatsProjection statsProjection, String property) {
        return goldPriceStatRepository.findById(statsProjection.getMaxId().longValue())
                .map(goldPriceMapper::toResponseWithServerName)
                .orElseThrow(() -> new PopulationNotFoundException("No max population found for " + property));
    }

    private GoldPriceResponse getMin(StatsProjection statsProjection, String property) {
        return goldPriceStatRepository.findById(statsProjection.getMinId().longValue())
                .map(goldPriceMapper::toResponseWithServerName)
                .orElseThrow(() -> new PopulationNotFoundException("No min population found for " + property));
    }

    private StatsProjection findForServer(String serverIdentifier) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return goldPriceStatRepository.findStatsForServer(serverId);
    }
}
