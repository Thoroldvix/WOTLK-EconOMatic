package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.shared.SearchRequest;
import com.thoroldvix.economatic.shared.TimeRange;
import com.thoroldvix.economatic.shared.SpecificationBuilder;
import com.thoroldvix.economatic.shared.StringEnumConverter;
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

import static com.thoroldvix.economatic.error.ErrorMessages.*;
import static com.thoroldvix.economatic.server.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.ValidationUtils.notEmpty;
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


    private static void validateInputs(TimeRange timeRange, Pageable pageable) {
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
    }

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

    @Override
    public GoldPriceListResponse getAllRecent() {
        List<GoldPrice> prices = goldPriceRepository.findAllRecent();
        notEmpty(prices,
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    @Override
    public GoldPricePageResponse search(@Valid SearchRequest searchRequest, Pageable pageable) {
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL);

        Page<GoldPrice> prices = findAllForSearch(searchRequest, pageable);
        notEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));

        return goldPriceMapper.toPageResponse(prices);
    }

    @Override
    public GoldPricePageResponse getForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        validateInputs(timeRange, pageable);

        Page<GoldPrice> prices = findAllForServer(serverIdentifier, timeRange, pageable);
        notEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException("No prices found for server identifier %s and time range: %s-%s".formatted(
                        serverIdentifier, timeRange.start(), timeRange.end())));

        return goldPriceMapper.toPageResponse(prices);
    }

    @Override
    public GoldPriceResponse getRecentForServer(String serverIdentifier) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        GoldPrice price = findRecentForServer(serverIdentifier)
                .orElseThrow(() -> new GoldPriceNotFoundException("No prices found for server " + serverIdentifier));

        return goldPriceMapper.toResponse(price);
    }

    @Override
    public GoldPriceListResponse getRecentForRegion(String regionName) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);

        List<GoldPrice> prices = findRecentForRegion(regionName);
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for region " + regionName));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    @Override
    public GoldPriceListResponse getRecentForFaction(String factionName) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);

        List<GoldPrice> prices = findRecentForFaction(factionName);
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for faction " + factionName));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    @Override
    public GoldPriceListResponse getRecentForServerList(@Valid GoldPriceRequest request) {
        requireNonNull(request, "Gold price request cannot be null");

        List<GoldPrice> prices = findRecentForServerList(request.serverList());
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for server list"));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    @Override
    @Transactional
    public void saveAll(List<GoldPrice> pricesToSave) {
        notEmpty(pricesToSave, () -> new IllegalArgumentException("Prices cannot be null or empty"));

        goldPriceRepository.saveAll(pricesToSave);
    }

    private Page<GoldPrice> findAllForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable) {
        int serverId = serverService.getServer(serverIdentifier).id();

        return goldPriceRepository.findAllForServerAndTimeRange(serverId, timeRange.start(), timeRange.end(), pageable);
    }

    private Page<GoldPrice> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<GoldPrice> spec = SpecificationBuilder.from(searchRequest);
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
        return serverList.stream()
                .map(server -> serverService.getServer(server).id())
                .collect(Collectors.toSet());
    }
}