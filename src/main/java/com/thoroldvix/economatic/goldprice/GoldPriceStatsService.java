package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.shared.ServerService;
import com.thoroldvix.economatic.shared.StatsProjection;
import com.thoroldvix.economatic.shared.StringEnumConverter;
import com.thoroldvix.economatic.shared.TimeRange;
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
public class GoldPriceStatsService {

    private final ServerService serverServiceImpl;
    private final GoldPriceStatRepository goldPriceStatRepository;
    private final GoldPriceStatMapper goldPriceStatMapper;

    public GoldPriceStatResponse getForServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier,
            @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange) {
        return generateGoldPriceStatResponse(() -> findForServer(serverIdentifier, timeRange));
    }

    public GoldPriceStatResponse getForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName,
            @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange) {
        return generateGoldPriceStatResponse(() -> findForRegion(regionName, timeRange));
    }

    public GoldPriceStatResponse getForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName,
            @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange) {
        return generateGoldPriceStatResponse(() -> findForFaction(factionName, timeRange));
    }

    public GoldPriceStatResponse getForAll(
            @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange) {
        return generateGoldPriceStatResponse(() -> findForTimeRange(timeRange));
    }

    private GoldPriceStatResponse generateGoldPriceStatResponse(Supplier<StatsProjection> statsSupplier) {
        StatsProjection statsProjection = statsSupplier.get();
        return goldPriceStatMapper.toResponse(statsProjection, goldPriceStatRepository);
    }

    private StatsProjection findForServer(String serverIdentifier, TimeRange timeRange) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
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
}
