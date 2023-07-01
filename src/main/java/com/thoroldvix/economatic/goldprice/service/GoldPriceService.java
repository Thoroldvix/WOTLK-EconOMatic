package com.thoroldvix.economatic.goldprice.service;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceListResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceRequest;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPricePageResponse;
import com.thoroldvix.economatic.goldprice.error.GoldPriceNotFoundException;
import com.thoroldvix.economatic.goldprice.mapper.GoldPriceMapper;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.goldprice.repository.GoldPriceRepository;
import com.thoroldvix.economatic.population.error.PopulationNotFoundException;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.service.ServerService;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import com.thoroldvix.economatic.shared.dto.TimeRange;
import com.thoroldvix.economatic.shared.service.SearchSpecification;
import com.thoroldvix.economatic.shared.util.StringEnumConverter;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thoroldvix.economatic.server.error.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.error.ErrorMessages.PAGEABLE_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.shared.error.ErrorMessages.TIME_RANGE_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.shared.util.Utils.notEmpty;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoldPriceService {

    public static final String NO_PRICES_FOUND = "No prices found";
    private final ServerService serverService;
    private final GoldPriceRepository goldPriceRepository;
    private final GoldPriceMapper goldPriceMapper;
    private final SearchSpecification<GoldPrice> searchSpecification;

    @Cacheable("gold-price-cache")
    public GoldPricePageResponse getAll(
            @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange,
            @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
            Pageable pageable) {
        Page<GoldPrice> page = findAllForTimeRange(timeRange, pageable);
        notEmpty(page.getContent(),
                () -> new GoldPriceNotFoundException("No prices found for time range: %s-%s".formatted(timeRange.start(), timeRange.end())));
        return goldPriceMapper.toPageResponse(page);
    }


    public GoldPriceListResponse getAllRecent() {
        List<GoldPrice> prices = goldPriceRepository.findAllRecent();
        notEmpty(prices,
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));
        return goldPriceMapper.toGoldPriceList(prices);
    }

    @Cacheable("gold-price-cache")
    public GoldPricePageResponse search(@Valid SearchRequest searchRequest,
                                        @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
                                         Pageable pageable) {
        Page<GoldPrice> prices = findAllForSearch(searchRequest, pageable);
        notEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));
        return goldPriceMapper.toPageResponse(prices);
    }

    @Cacheable("gold-price-cache")
    public GoldPricePageResponse getAllForServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier,
            @NotNull(message = TIME_RANGE_CANNOT_BE_NULL)
            TimeRange timeRange,
            @NotNull(message = PAGEABLE_CANNOT_BE_NULL)
            Pageable pageable) {
        Page<GoldPrice> prices = findAllForServer(serverIdentifier, timeRange, pageable);
        notEmpty(prices.getContent(),
                () -> new PopulationNotFoundException(String.format("No prices found for server identifier %s and time range: %s-%s",
                        serverIdentifier, timeRange.start(), timeRange.end())));
        return goldPriceMapper.toPageResponse(prices);
    }


    public GoldPriceListResponse getRecentForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName) {
        List<GoldPrice> prices = findRecentForRegion(regionName);
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for region " + regionName));
        return goldPriceMapper.toGoldPriceList(prices);
    }


    public GoldPriceListResponse getRecentForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName) {
        List<GoldPrice> prices = findRecentForFaction(factionName);
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for faction " + factionName));
        return goldPriceMapper.toGoldPriceList(prices);
    }


    public GoldPriceResponse getRecentForServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier) {
        GoldPrice price = findRecentForServer(serverIdentifier)
                .orElseThrow(() -> new GoldPriceNotFoundException("No prices found for server " + serverIdentifier));
        return goldPriceMapper.toResponse(price);
    }


    public GoldPriceListResponse getRecentForServerList(@Valid @NotNull(message = "Gold price request cannot be null")
                                                     GoldPriceRequest request) {
        List<GoldPrice> prices = findRecentForServerList(request.serverList());
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for server list"));
        return goldPriceMapper.toGoldPriceList(prices);
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

    private Optional<GoldPrice> findRecentForServer(String serverIdentifier) {
        int serverId = serverService.getServer(serverIdentifier).id();
        return goldPriceRepository.findRecentForServer(serverId);
    }

    private List<GoldPrice> findRecentForServerList(Set<String> serverList) {
        Set<Integer> serverIds = getServerIds(serverList);
        return goldPriceRepository.findRecentForServers(serverIds);
    }

    private Set<Integer> getServerIds(Set<String> serverList) {
        return serverList.parallelStream()
                .map(server -> serverService.getServer(server).id())
                .collect(Collectors.toSet());
    }
}