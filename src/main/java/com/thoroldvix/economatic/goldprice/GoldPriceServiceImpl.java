package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.common.dto.TimeRange;
import com.thoroldvix.economatic.common.util.StringEnumConverter;
import com.thoroldvix.economatic.search.SearchRequest;
import com.thoroldvix.economatic.search.SpecificationBuilder;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import jakarta.validation.Valid;
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

import static com.thoroldvix.economatic.common.util.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.error.ErrorMessages.*;
import static java.util.Objects.requireNonNull;

@Service
@Validated
@RequiredArgsConstructor
@Cacheable("gold-price-cache")
@Transactional(readOnly = true)
class GoldPriceServiceImpl implements GoldPriceService {

    public static final String NO_PRICES_FOUND = "No prices found";

    private final ServerService serverService;
    private final GoldPriceRepository goldPriceRepository;
    private final GoldPriceMapper goldPriceMapper;

    @Override
    public GoldPriceResponse getForId(long id) {
        return goldPriceRepository.findById(id).map(goldPriceMapper::toResponse)
                .orElseThrow(() -> new GoldPriceNotFoundException("No gold price found with id " + id));
    }

    @Override
    public GoldPricePageResponse getAll(TimeRange timeRange, Pageable pageable) {
        validateInputs(timeRange, pageable);
        Page<GoldPrice> page = findAllForTimeRange(timeRange, pageable);
        notEmpty(page.getContent(),
                () -> new GoldPriceNotFoundException("No prices found for time range: %s-%s".formatted(timeRange.start(), timeRange.end())));
        return goldPriceMapper.toPageResponse(page);
    }

    private Page<GoldPrice> findAllForTimeRange(TimeRange timeRange, Pageable pageable) {
        return goldPriceRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageable);
    }

    @Override
    public GoldPricePageResponse getForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);
        validateInputs(timeRange, pageable);

        ServerResponse server = serverService.getServer(serverIdentifier);
        Page<GoldPrice> prices = findAllForServer(server, timeRange, pageable);

        notEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException("No prices found for server identifier %s and time range: %s-%s".formatted(
                        serverIdentifier, timeRange.start(), timeRange.end())));

        return goldPriceMapper.toPageResponse(prices);
    }

    private void validateInputs(TimeRange timeRange, Pageable pageable) {
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL.message);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL.message);
    }

    private Page<GoldPrice> findAllForServer(ServerResponse server, TimeRange timeRange, Pageable pageable) {
        return goldPriceRepository.findAllForServerAndTimeRange(server.id(), timeRange.start(), timeRange.end(), pageable);
    }

    @Override
    public GoldPriceListResponse getAllRecent() {
        List<GoldPrice> prices = goldPriceRepository.findAllRecent();
        notEmpty(prices,
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    @Override
    public GoldPricePageResponse search(@Valid SearchRequest searchRequest, Pageable pageable) {
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL.message);
        requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL.message);

        Page<GoldPrice> prices = findAllForSearch(searchRequest, pageable);
        notEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));

        return goldPriceMapper.toPageResponse(prices);
    }

    private Page<GoldPrice> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<GoldPrice> spec = SpecificationBuilder.from(searchRequest);
        return goldPriceRepository.findAll(spec, pageable);
    }

    @Override
    public GoldPriceResponse getRecentForServer(String serverIdentifier) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        ServerResponse server = serverService.getServer(serverIdentifier);
        GoldPrice price = findRecentForServer(server)
                .orElseThrow(() -> new GoldPriceNotFoundException("No prices found for server " + serverIdentifier));

        return goldPriceMapper.toResponse(price);
    }

    private Optional<GoldPrice> findRecentForServer(ServerResponse server) {
        return goldPriceRepository.findRecentForServer(server.id());
    }

    @Override
    public GoldPriceListResponse getRecentForRegion(String regionName) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);

        List<GoldPrice> prices = findRecentForRegion(regionName);
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for region " + regionName));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    private List<GoldPrice> findRecentForRegion(String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return goldPriceRepository.findRecentForRegion(region.ordinal());
    }

    @Override
    public GoldPriceListResponse getRecentForFaction(String factionName) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);

        List<GoldPrice> prices = findRecentForFaction(factionName);
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for faction " + factionName));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    private List<GoldPrice> findRecentForFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return goldPriceRepository.findRecentForFaction(faction.ordinal());
    }

    @Override
    public GoldPriceListResponse getRecentForServerList(@Valid GoldPriceRequest request) {
        requireNonNull(request, "Gold price request cannot be null");

        Set<Integer> serverIds = getServerIds(request.serverList());
        List<GoldPrice> prices = findRecentForServerIds(serverIds);
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for server list"));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    private Set<Integer> getServerIds(Set<String> serverList) {
        return serverList.stream()
                .map(server -> serverService.getServer(server).id())
                .collect(Collectors.toSet());
    }

    private List<GoldPrice> findRecentForServerIds(Set<Integer> serverIds) {
        return goldPriceRepository.findRecentForServerIds(serverIds);
    }

    @Override
    @Transactional
    public void saveAll(List<GoldPrice> pricesToSave) {
        notEmpty(pricesToSave, () -> new IllegalArgumentException("Prices cannot be null or empty"));

        goldPriceRepository.saveAll(pricesToSave);
    }
}