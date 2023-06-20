package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.population.PopulationNotFoundException;
import com.thoroldvix.pricepal.server.Faction;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.server.ServerService;
import com.thoroldvix.pricepal.shared.SearchRequest;
import com.thoroldvix.pricepal.shared.SearchSpecification;
import com.thoroldvix.pricepal.shared.StringEnumConverter;
import com.thoroldvix.pricepal.shared.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.thoroldvix.pricepal.server.ServerErrorMessages.*;
import static com.thoroldvix.pricepal.shared.ErrorMessages.PAGEABLE_CANNOT_BE_NULL;
import static com.thoroldvix.pricepal.shared.ErrorMessages.SEARCH_REQUEST_CANNOT_BE_NULL;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateCollectionNotNullOrEmpty;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateStringNonNullOrEmpty;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoldPriceService {

    public static final String NO_PRICES_FOUND = "No prices found";
    private final ServerService serverServiceImpl;
    private final GoldPriceRepository goldPriceRepository;
    private final GoldPriceMapper goldPriceMapper;
    private final SearchSpecification<GoldPrice> searchSpecification;

    public GoldPricesPagedResponse getAll(TimeRange timeRange, Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        Page<GoldPrice> page = findAllForTimeRange(timeRange, pageable);
        validateCollectionNotNullOrEmpty(page.getContent(),
                () -> new GoldPriceNotFoundException(String.format("No prices found for time range: %s-%s", timeRange.start(), timeRange.end())));

        return goldPriceMapper.toPagedPricesResponse(page);
    }

    public GoldPricesResponse getAllRecent() {
        List<GoldPrice> prices = goldPriceRepository.findAllRecent();
        validateCollectionNotNullOrEmpty(prices,
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));
        return goldPriceMapper.toPricesResponse(prices);
    }

    public GoldPricesPagedResponse search(SearchRequest searchRequest,
                                          Pageable pageable) {
        Objects.requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL);
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        Page<GoldPrice> prices = findAllForSearch(searchRequest, pageable);
        validateCollectionNotNullOrEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));
        return goldPriceMapper.toPagedPricesResponse(prices);
    }

    public GoldPricesPagedResponse getAllForServer(String serverIdentifier, TimeRange timeRange,
                                                   Pageable pageable) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);

        Page<GoldPrice> prices = findAllForServer(serverIdentifier, timeRange, pageable);
        validateCollectionNotNullOrEmpty(prices.getContent(),
                () -> new PopulationNotFoundException(String.format("No prices found for server identifier %s and time range: %s-%s",
                        serverIdentifier, timeRange.start(), timeRange.end())));

        return goldPriceMapper.toPagedPricesResponse(prices);
    }

    public GoldPricesResponse getRecentForRegion(String regionName) {
        validateStringNonNullOrEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        List<GoldPrice> prices = findRecentForRegion(regionName);
        validateCollectionNotNullOrEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for region " + regionName));
        return goldPriceMapper.toPricesRegionResponse(prices);
    }

    public GoldPricesResponse getRecentForFaction(String factionName) {
        validateStringNonNullOrEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        List<GoldPrice> prices = findRecentForFaction(factionName);
        validateCollectionNotNullOrEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for faction " + factionName));
        return goldPriceMapper.toPricesFactionResponse(prices);
    }

    public GoldPriceResponse getRecentForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        GoldPrice price = findRecentForServer(serverIdentifier);
        return goldPriceMapper.toResponseWithServer(price);
    }

    @Transactional
    public void saveAll(List<GoldPrice> pricesToSave) {
        validateCollectionNotNullOrEmpty(pricesToSave,
                () -> new IllegalArgumentException("Prices cannot be null or empty"));
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