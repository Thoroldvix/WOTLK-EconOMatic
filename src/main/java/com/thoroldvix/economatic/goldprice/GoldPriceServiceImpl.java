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
import java.util.Set;
import java.util.stream.Collectors;

import static com.thoroldvix.economatic.common.util.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.error.ErrorMessages.*;

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
        Page<GoldPrice> page = goldPriceRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageable);
        notEmpty(page.getContent(),
                () -> new GoldPriceNotFoundException("No prices found for time range: %s-%s".formatted(timeRange.start(), timeRange.end())));
        return goldPriceMapper.toPageResponse(page);
    }

    @Override
    public GoldPricePageResponse getForServer(String serverIdentifier, TimeRange timeRange, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        ServerResponse server = serverService.getServer(serverIdentifier);
        Page<GoldPrice> prices = goldPriceRepository
                .findAllForServerAndTimeRange(server.id(), timeRange.start(), timeRange.end(), pageable);

        notEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException("No prices found for server identifier %s and time range: %s-%s".formatted(
                        serverIdentifier, timeRange.start(), timeRange.end())));

        return goldPriceMapper.toPageResponse(prices);
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
        Specification<GoldPrice> spec = SpecificationBuilder.from(searchRequest);
        Page<GoldPrice> prices = goldPriceRepository.findAll(spec, pageable);
        notEmpty(prices.getContent(),
                () -> new GoldPriceNotFoundException(NO_PRICES_FOUND));

        return goldPriceMapper.toPageResponse(prices);
    }

    @Override
    public GoldPriceResponse getRecentForServer(String serverIdentifier) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        ServerResponse server = serverService.getServer(serverIdentifier);
        GoldPrice price = goldPriceRepository.findRecentForServer(server.id())
                .orElseThrow(() -> new GoldPriceNotFoundException("No prices found for server " + serverIdentifier));

        return goldPriceMapper.toResponse(price);
    }

    @Override
    public GoldPriceListResponse getRecentForRegion(String regionName) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);

        Region region = StringEnumConverter.fromString(regionName, Region.class);
        List<GoldPrice> prices = goldPriceRepository.findRecentForRegion(region.ordinal());
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for region " + regionName));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    @Override
    public GoldPriceListResponse getRecentForFaction(String factionName) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);

        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        List<GoldPrice> prices = goldPriceRepository.findRecentForFaction(faction.ordinal());
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for faction " + factionName));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    @Override
    public GoldPriceListResponse getRecentForServerList(@Valid GoldPriceRequest request) {
        Set<Integer> serverIds = getServerIds(request.serverList());
        List<GoldPrice> prices = goldPriceRepository.findRecentForServerIds(serverIds);
        notEmpty(prices, () -> new GoldPriceNotFoundException("No prices found for server list"));

        return goldPriceMapper.toGoldPriceList(prices);
    }

    private Set<Integer> getServerIds(Set<String> serverList) {
        return serverList.stream()
                .map(server -> serverService.getServer(server).id())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void saveAll(List<GoldPrice> pricesToSave) {
        notEmpty(pricesToSave, () -> new IllegalArgumentException("Prices cannot be null or empty"));

        goldPriceRepository.saveAll(pricesToSave);
    }
}