package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.population.PopulationNotFoundException;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.shared.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.thoroldvix.economatic.server.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.ErrorMessages.PAGEABLE_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.shared.ErrorMessages.TIME_RANGE_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.shared.ValidationUtils.validateCollectionNotEmpty;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoldPriceService {

    public static final String NO_PRICES_FOUND = "No prices found";
    private final ServerService serverServiceImpl;
    private final GoldPriceRepository goldPriceRepository;
    private final GoldPriceMapper goldPriceMapper;
    private final SearchSpecification<GoldPrice> searchSpecification;

    public GoldPricesPagedResponse getAll(
            @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange,
            @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
            Pageable pageable) {
        Page<GoldPrice> page = findAllForTimeRange(timeRange, pageable);
        validateCollectionNotEmpty(page.getContent(),
                () -> new GoldPriceNotFoundException("No prices found for time range: %s-%s".formatted(timeRange.start(), timeRange.end())));
        return goldPriceMapper.toPagedPricesResponse(page);
    }

    public GoldPricesResponse getAllRecent() {
        List<GoldPrice> prices = goldPriceRepository.findAllRecent();
        validateCollectionNotEmpty(prices,
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));
        return goldPriceMapper.toPricesResponse(prices);
    }

    public GoldPricesPagedResponse search(@Valid SearchRequest searchRequest,
                                          @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
                                          Pageable pageable) {
        Page<GoldPrice> prices = findAllForSearch(searchRequest, pageable);
        validatePricesNotEmpty(prices);
        return goldPriceMapper.toPagedPricesResponse(prices);
    }

    private static void validatePricesNotEmpty(Page<GoldPrice> prices) {
        validateCollectionNotEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));
    }

    public GoldPricesPagedResponse getAllForServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier,
            @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange,
            @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
            Pageable pageable) {
        Page<GoldPrice> prices = findAllForServer(serverIdentifier, timeRange, pageable);
        validateCollectionNotEmpty(prices.getContent(),
                () -> new PopulationNotFoundException(String.format("No prices found for server identifier %s and time range: %s-%s",
                        serverIdentifier, timeRange.start(), timeRange.end())));
        return goldPriceMapper.toPagedPricesResponse(prices);
    }

    public GoldPricesResponse getRecentForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName) {
        List<GoldPrice> prices = findRecentForRegion(regionName);
        validateCollectionNotEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for region " + regionName));
        return goldPriceMapper.toPricesRegionResponse(prices);
    }

    public GoldPricesResponse getRecentForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName) {
        List<GoldPrice> prices = findRecentForFaction(factionName);
        validateCollectionNotEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for faction " + factionName));
        return goldPriceMapper.toPricesFactionResponse(prices);
    }

    public GoldPriceResponse getRecentForServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier) {
        GoldPrice price = findRecentForServer(serverIdentifier);
        return goldPriceMapper.toResponseWithServer(price);
    }

    @Transactional
    public void saveAll(
            @NotEmpty(message = "Prices cannot be null or empty")
            List<GoldPrice> pricesToSave) {
        goldPriceRepository.saveAll(pricesToSave);
    }

    private Page<GoldPrice> findAllForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return goldPriceRepository.findAllForServerAndTimeRange(serverId, timeRange.start(), timeRange.end(), pageable);
    }

    private Page<GoldPrice> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<GoldPrice> spec = searchSpecification.create(searchRequest.globalOperator(), searchRequest.searchCriteria());
        return goldPriceRepository.findAll(spec, pageable);
    }

    private Page<GoldPrice> findAllForTimeRange(TimeRange timeRange, Pageable pageable) {
        return goldPriceRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageable);
    }

    private List<GoldPrice> findRecentForRegion(String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return goldPriceRepository.findRecentForRegion(region.ordinal());
    }

    private List<GoldPrice> findRecentForFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return goldPriceRepository.findRecentForFaction(faction.ordinal());
    }


    private GoldPrice findRecentForServer(String serverIdentifier) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return goldPriceRepository.findRecentForServer(serverId)
                .orElseThrow(() -> new GoldPriceNotFoundException("No prices found for server " + serverIdentifier));
    }
}