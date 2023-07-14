package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.dto.TimeRange;
import com.thoroldvix.economatic.item.ItemResponse;
import com.thoroldvix.economatic.item.ItemService;
import com.thoroldvix.economatic.search.SearchRequest;
import com.thoroldvix.economatic.search.SpecificationBuilder;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.util.StringEnumConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thoroldvix.economatic.error.ErrorMessages.*;
import static com.thoroldvix.economatic.item.ItemErrorMessages.ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY;
import static com.thoroldvix.economatic.server.ServerErrorMessages.*;
import static com.thoroldvix.economatic.util.ValidationUtils.isCollectionEmpty;
import static com.thoroldvix.economatic.util.ValidationUtils.notEmpty;
import static java.util.Objects.requireNonNull;


@Service
@Cacheable("item-price-cache")
@Validated
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemPriceServiceImpl implements ItemPriceService {


    private final ItemService itemService;
    private final ServerService serverService;
    private final ItemPriceRepository itemPriceRepository;
    private final ItemPriceMapper itemPriceMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public ItemPricePageResponse getRecentForServer(String serverIdentifier, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);

        ServerResponse server = serverService.getServer(serverIdentifier);
        Page<ItemPrice> page = findRecentForServer(server, pageable);

        notEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No recent item prices found for server identifier " + serverIdentifier));

        return itemPriceMapper.toPageResponse(page);
    }

    @Override
    public ItemPriceListResponse getRecentForRegion(String regionName, String itemIdentifier) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        ItemResponse item = itemService.getItem(itemIdentifier);
        List<ItemPrice> itemPrices = findRecentForRegionAndItem(regionName, item);

        notEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for region and item identifier " + regionName + " " + itemIdentifier));

        return itemPriceMapper.toItemPriceList(itemPrices);
    }

    @Override
    public ItemPriceListResponse getRecentForFaction(String factionName, String itemIdentifier) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        ItemResponse item = itemService.getItem(itemIdentifier);
        List<ItemPrice> itemPrices = findRecentForFactionAndItem(factionName, item);

        notEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for faction and item identifier " + factionName + " " + itemIdentifier));

        return itemPriceMapper.toItemPriceList(itemPrices);
    }

    @Override
    public ItemPriceListResponse getRecentForServer(String serverIdentifier, String itemIdentifier) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        ServerResponse server = serverService.getServer(serverIdentifier);
        ItemResponse item = itemService.getItem(itemIdentifier);
        List<ItemPrice> itemPrices = findRecentForServerAndItem(server, item);

        notEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for server identifier %s and item identifier %s"
                        .formatted(serverIdentifier, itemIdentifier)));

        return itemPriceMapper.toItemPriceList(itemPrices);
    }

    @Override
    public ItemPricePageResponse search(@Valid SearchRequest searchRequest, Pageable pageable) {
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL);

        Page<ItemPrice> page = findAllForSearch(searchRequest, pageable);
        notEmpty(page.getContent(), () -> new ItemPriceNotFoundException("No item prices found for search request"));

        return itemPriceMapper.toPageResponse(page);
    }

    @Override
    public ItemPricePageResponse getForServer(String serverIdentifier, String itemIdentifier, TimeRange timeRange, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        ServerResponse server = serverService.getServer(serverIdentifier);
        ItemResponse item = itemService.getItem(itemIdentifier);
        Page<ItemPrice> page = findForServerAndTimeRange(server, item, timeRange, pageable);

        notEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No item prices found for time range %s for server identifier %s and item identifier %s"
                        .formatted(timeRange, serverIdentifier, itemIdentifier)));

        return itemPriceMapper.toPageResponse(page);
    }

    @Override
    public ItemPricePageResponse getRecentForItemListAndServers(@Valid ItemPriceRequest request, Pageable pageable) {
        requireNonNull(request, "Item price request cannot be null");
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);

        Set<Integer> itemIds = getItemIds(request.itemList());
        Set<Integer> serverIds = getServerIds(request.serverList());
        Page<ItemPrice> page = findRecentForItemsAndServers(serverIds, itemIds, pageable);

        notEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No recent prices found for item list"));

        return itemPriceMapper.toPageResponse(page);
    }

    @Override
    @Transactional
    public void saveAll(List<ItemPrice> itemPricesToSave) {
        requireNonNull(itemPricesToSave, "Item prices cannot be null");

        itemPriceRepository.saveAll(itemPricesToSave, jdbcTemplate);
    }

    private Page<ItemPrice> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<ItemPrice> specification = SpecificationBuilder.from(searchRequest);
        return itemPriceRepository.findAll(specification, pageable);
    }


    private Page<ItemPrice> findRecentForServer(ServerResponse server, Pageable pageable) {
        return itemPriceRepository.findRecentForServer(server.id(), pageable);
    }


    private Page<ItemPrice> findForServerAndTimeRange(ServerResponse server,
                                                      ItemResponse item,
                                                      TimeRange timeRange,
                                                      Pageable pageable) {

        return itemPriceRepository.findForServerAndTimeRange(server.id(), item.id(), timeRange.start(), timeRange.end(), pageable);
    }


    private List<ItemPrice> findRecentForServerAndItem(ServerResponse server, ItemResponse item) {
        return itemPriceRepository.findRecentForServerAndItem(server.id(), item.id());
    }


    private List<ItemPrice> findRecentForRegionAndItem(String regionName, ItemResponse item) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return itemPriceRepository.findRecentForRegionAndItem(region.ordinal(), item.id());
    }


    private List<ItemPrice> findRecentForFactionAndItem(String factionName, ItemResponse item) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return itemPriceRepository.findRecentForFactionAndItem(faction.ordinal(), item.id());
    }


    private Page<ItemPrice> findRecentForItemsAndServers(Set<Integer> serverIds, Set<Integer> itemIds, Pageable pageable) {
        if (isCollectionEmpty(serverIds)) {
            return itemPriceRepository.findRecentForItemList(itemIds, pageable);
        }
        return itemPriceRepository.findRecentForItemsAndServers(itemIds, serverIds, pageable);
    }

    private Set<Integer> getServerIds(Set<String> serverList) {
        return serverList.parallelStream()
                .map(server -> serverService.getServer(server).id())
                .collect(Collectors.toSet());
    }

    private Set<Integer> getItemIds(Set<String> itemList) {
        return itemList.parallelStream()
                .map(item -> itemService.getItem(item).id())
                .collect(Collectors.toSet());
    }
}
