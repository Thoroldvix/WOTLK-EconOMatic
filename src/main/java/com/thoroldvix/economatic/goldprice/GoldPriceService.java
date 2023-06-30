package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.population.PopulationNotFoundException;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.shared.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final ServerService serverService;
    private final GoldPriceRepository goldPriceRepository;
    private final GoldPriceMapper goldPriceMapper;
    private final GoldPricesMapper goldPricesMapper;
    private final SearchSpecification<GoldPrice> searchSpecification;

    @Cacheable("gold-price-cache")
    public GoldPricesPagedResponse getAll(
            @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange,
            @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
            Pageable pageable) {
        Page<GoldPrice> page = findAllForTimeRange(timeRange, pageable);
        validateCollectionNotEmpty(page.getContent(),
                () -> new GoldPriceNotFoundException("No prices found for time range: %s-%s".formatted(timeRange.start(), timeRange.end())));
        return goldPricesMapper.toPaged(page);
    }


    public GoldPricesResponse getAllRecent() {
        List<GoldPrice> prices = goldPriceRepository.findAllRecent();
        validateCollectionNotEmpty(prices,
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));
        return goldPricesMapper.toResponse(prices);
    }

    @Cacheable("gold-price-cache")
    public GoldPricesPagedResponse search(@Valid SearchRequest searchRequest,
                                          @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
                                          Pageable pageable) {
        Page<GoldPrice> prices = findAllForSearch(searchRequest, pageable);
        validateCollectionNotEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));
        return goldPricesMapper.toPaged(prices);
    }

    @Cacheable("gold-price-cache")
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
        return goldPricesMapper.toPagedWithServer(prices);
    }


    public GoldPricesResponse getRecentForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName) {
        List<GoldPrice> prices = findRecentForRegion(regionName);
        validateCollectionNotEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for region " + regionName));
        return goldPricesMapper.toRegionResponse(prices);
    }


    public GoldPricesResponse getRecentForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName) {
        List<GoldPrice> prices = findRecentForFaction(factionName);
        validateCollectionNotEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for faction " + factionName));
        return goldPricesMapper.toFactionResponse(prices);
    }


    public GoldPriceResponse getRecentForServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier) {
        GoldPrice price = findRecentForServer(serverIdentifier);
        return goldPriceMapper.toResponseWithServer(price);
    }


    public GoldPricesResponse getRecentForServers(@Valid @NotNull(message = "Gold price request cannot be null")
                                                  GoldPriceRequest request) {
        List<GoldPrice> prices = findRecentForServers(request.serverList());
        return goldPricesMapper.toResponse(prices);
    }

    @Transactional
    public void saveAll(
            @NotEmpty(message = "Prices cannot be null or empty")
            List<GoldPrice> pricesToSave) {
        goldPriceRepository.saveAll(pricesToSave);
    }

    private Page<GoldPrice> findAllForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable) {
        int serverId = serverService.getServer(serverIdentifier).id();
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
        int serverId = serverService.getServer(serverIdentifier).id();
        return goldPriceRepository.findRecentForServer(serverId)
                .orElseThrow(() -> new GoldPriceNotFoundException("No prices found for server " + serverIdentifier));
    }


    private List<GoldPrice> findRecentForServers(Set<String> serverList) {
        Set<Integer> serverIds = getServerIds(serverList);
        return goldPriceRepository.findRecentForServers(serverIds);
    }

    private Set<Integer> getServerIds(Set<String> serverList) {
        return serverList.parallelStream()
                .map(server -> serverService.getServer(server).id())
                .collect(Collectors.toSet());
    }
}