package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.population.PopulationNotFoundException;
import com.thoroldvix.pricepal.population.PopulationResponse;
import com.thoroldvix.pricepal.server.Faction;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.shared.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static com.thoroldvix.pricepal.shared.ServerSearchCriteriaBuilder.getJoinCriteria;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoldPriceStatsService implements StatsService<GoldPriceResponse> {

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
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        StatsProjection statsProjection = goldPriceStatRepository.findStatsByRegion(region.ordinal());
        return getStatResponse(statsProjection, regionName);
    }

    @Override
    public StatsResponse<GoldPriceResponse> getForFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        StatsProjection statsProjection = goldPriceStatRepository.findStatsByFaction(faction.ordinal());
        return getStatResponse(statsProjection, factionName);
    }

    @Override
    public StatsResponse<GoldPriceResponse> getForAll(int timeRangeInDays) {
        LocalDateTime start = LocalDateTime.now().minusDays(timeRangeInDays);
        LocalDateTime end = LocalDateTime.now();
        StatsProjection statsProjection = goldPriceStatRepository.findStatsForAll(start, end);
        return getStatResponse(statsProjection, "all");
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
                .map(goldPriceMapper::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No max population found for " + property));
    }

    private GoldPriceResponse getMin(StatsProjection statsProjection, String property) {
        return goldPriceStatRepository.findById(statsProjection.getMinId().longValue())
                .map(goldPriceMapper::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No min population found for " + property));
    }

    private StatsProjection findForServer(String serverIdentifier) {
        try {
            int serverId = Integer.parseInt(serverIdentifier);
            return goldPriceStatRepository.findStatsByServerId(serverId);
        } catch (NumberFormatException e) {
            return goldPriceStatRepository.findStatsByServerUniqueName(serverIdentifier);
        }
    }
}
