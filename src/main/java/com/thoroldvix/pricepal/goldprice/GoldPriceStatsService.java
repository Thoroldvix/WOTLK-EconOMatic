package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.server.Faction;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.server.ServerService;
import com.thoroldvix.pricepal.shared.StatsProjection;
import com.thoroldvix.pricepal.shared.StringEnumConverter;
import com.thoroldvix.pricepal.shared.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.thoroldvix.pricepal.server.ServerErrorMessages.SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoldPriceStatsService {

    private final ServerService serverServiceImpl;
    private final GoldPriceStatRepository goldPriceStatRepository;
    private final GoldPriceStatMapper goldPriceStatMapper;

    public GoldPriceStatResponse getForServer(String serverIdentifier, TimeRange timeRange) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        StatsProjection statsProjection = findForServer(serverIdentifier, timeRange);
        return goldPriceStatMapper.toResponse(statsProjection, goldPriceStatRepository);
    }

    public GoldPriceStatResponse getForRegion(String regionName, TimeRange timeRange) {
        validateStringNonNullOrEmpty(regionName, "Region serverName cannot be null or empty");
        StatsProjection statsProjection = findForRegion(regionName, timeRange);
        return goldPriceStatMapper.toResponse(statsProjection, goldPriceStatRepository);
    }

    public GoldPriceStatResponse getForFaction(String factionName, TimeRange timeRange) {
        validateStringNonNullOrEmpty(factionName, "Faction serverName cannot be null or empty");
        StatsProjection statsProjection = findForFaction(factionName, timeRange);
        return goldPriceStatMapper.toResponse(statsProjection, goldPriceStatRepository);
    }

    public GoldPriceStatResponse getForAll(TimeRange timeRange) {
        StatsProjection statsProjection = findForTimeRange(timeRange);
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
