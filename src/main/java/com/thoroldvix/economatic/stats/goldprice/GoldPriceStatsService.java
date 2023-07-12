package com.thoroldvix.economatic.stats.goldprice;

import com.thoroldvix.economatic.goldprice.GoldPriceMapper;
import com.thoroldvix.economatic.goldprice.GoldPriceNotFoundException;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.shared.dto.TimeRange;
import com.thoroldvix.economatic.shared.StringEnumConverter;
import com.thoroldvix.economatic.stats.StatsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static com.thoroldvix.economatic.error.ErrorMessages.TIME_RANGE_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.server.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.shared.ValidationUtils.validateStatsProjection;
import static java.util.Objects.requireNonNull;

@Service
@Cacheable("gold-price-stats-cache")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoldPriceStatsService {

    private final ServerService serverService;
    private final GoldPriceStatRepository goldPriceStatRepository;
    private final GoldPriceStatMapper goldPriceStatMapper;
    private final GoldPriceMapper goldPriceMapper;

    public GoldPriceStatResponse getForServer(String serverIdentifier, TimeRange timeRange) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        return generateGoldPriceStatResponse(() -> findForServer(serverIdentifier, timeRange));
    }

    public GoldPriceStatResponse getForRegion(String regionName, TimeRange timeRange) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        return generateGoldPriceStatResponse(() -> findForRegion(regionName, timeRange));
    }

    public GoldPriceStatResponse getForFaction(String factionName, TimeRange timeRange) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        return generateGoldPriceStatResponse(() -> findForFaction(factionName, timeRange));
    }

    public GoldPriceStatResponse getForAll(TimeRange timeRange) {
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        return generateGoldPriceStatResponse(() -> findForTimeRange(timeRange));
    }

    private GoldPriceStatResponse generateGoldPriceStatResponse(Supplier<StatsProjection> statsSupplier) {
        StatsProjection statsProjection = statsSupplier.get();
        validateStatsProjection(statsProjection);

        return getStatResponse(statsProjection);
    }

    private GoldPriceStatResponse getStatResponse(StatsProjection statsProjection) {
        GoldPriceResponse min = getMin(statsProjection);
        GoldPriceResponse max = getMax(statsProjection);

        return goldPriceStatMapper.toResponse(statsProjection, min, max);
    }

    private StatsProjection findForServer(String serverIdentifier, TimeRange timeRange) {
        int serverId = serverService.getServer(serverIdentifier).id();

        return goldPriceStatRepository.findStatsForServer(serverId, timeRange.start(), timeRange.end());
    }

    private StatsProjection findForRegion(String regionName, TimeRange timeRange) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);

        return goldPriceStatRepository.findForRegion(region.ordinal(), timeRange.start(), timeRange.end());
    }

    private StatsProjection findForTimeRange(TimeRange timeRange) {
        return goldPriceStatRepository.findStatsForAll(timeRange.start(), timeRange.end());
    }

    private StatsProjection findForFaction(String factionName, TimeRange timeRange) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);

        return goldPriceStatRepository.findForFaction(faction.ordinal(), timeRange.start(), timeRange.end());
    }

    private GoldPriceResponse getMax(StatsProjection goldPriceStat) {
        long maxId = goldPriceStat.getMaxId().longValue();

        return goldPriceStatRepository.findById(maxId).map(goldPriceMapper::toResponse)
                .orElseThrow(() -> new GoldPriceNotFoundException("No max gold price found with id " + maxId));
    }

    private GoldPriceResponse getMin(StatsProjection goldPriceStat) {
        long minId = goldPriceStat.getMinId().longValue();

        return goldPriceStatRepository.findById(minId).map(goldPriceMapper::toResponse)
                .orElseThrow(() -> new GoldPriceNotFoundException("No min gold price found with id " + minId));
    }
}
