package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.item.ItemService;
import com.thoroldvix.economatic.itemprice.dto.ItemPriceListResponse;
import com.thoroldvix.economatic.itemprice.dto.ItemPricePageResponse;
import com.thoroldvix.economatic.itemprice.dto.ItemPriceRequest;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import com.thoroldvix.economatic.shared.dto.TimeRange;
import com.thoroldvix.economatic.shared.SpecificationBuilder;
import com.thoroldvix.economatic.shared.StringEnumConverter;
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
import static com.thoroldvix.economatic.shared.ValidationUtils.isCollectionEmpty;
import static com.thoroldvix.economatic.shared.ValidationUtils.notEmpty;
import static java.util.Objects.requireNonNull;


@Service
@Cacheable("item-price-cache")
@Validated
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemPriceService {


    private final ItemService itemService;
    private final ServerService serverService;
    private final ItemPriceRepository itemPriceRepository;
    private final ItemPriceMapper itemPriceMapper;
    private final JdbcTemplate jdbcTemplate;


    public ItemPricePageResponse getRecentForServer(String serverIdentifier, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);

        Page<ItemPrice> page = findRecentForServer(serverIdentifier, pageable);
        notEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No recent item prices found for server identifier " + serverIdentifier));

        return itemPriceMapper.toPageResponse(page);
    }


    public ItemPriceListResponse getRecentForRegion(String regionName, String itemIdentifier) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        List<ItemPrice> itemPrices = findRecentForRegionAndItem(regionName, itemIdentifier);
        notEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for region and item identifier " + regionName + " " + itemIdentifier));

        return itemPriceMapper.toItemPriceList(itemPrices);
    }


    public ItemPriceListResponse getRecentForFaction(String factionName, String itemIdentifier) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        List<ItemPrice> itemPrices = findRecentForFactionAndItem(factionName, itemIdentifier);
        notEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for faction and item identifier " + factionName + " " + itemIdentifier));

        return itemPriceMapper.toItemPriceList(itemPrices);
    }


    public ItemPriceListResponse getRecentForServer(String serverIdentifier, String itemIdentifier) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        List<ItemPrice> itemPrices = findRecentForServerAndItem(serverIdentifier, itemIdentifier);
        notEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for server identifier %s and item identifier %s"
                        .formatted(serverIdentifier, itemIdentifier)));

        return itemPriceMapper.toItemPriceList(itemPrices);
    }


    public ItemPricePageResponse search(@Valid SearchRequest searchRequest, Pageable pageable) {
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL);

        Page<ItemPrice> page = findAllForSearch(searchRequest, pageable);
        notEmpty(page.getContent(), () -> new ItemPriceNotFoundException("No item prices found for search request"));

        return itemPriceMapper.toPageResponse(page);
    }


    public ItemPricePageResponse getForServer(String serverIdentifier, String itemIdentifier, TimeRange timeRange, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        requireNonNull(timeRange, TIME_RANGE_CANNOT_BE_NULL);

        Page<ItemPrice> page = findForServerAndTimeRange(serverIdentifier, itemIdentifier, timeRange, pageable);
        notEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No item prices found for time range %s for server identifier %s and item identifier %s"
                        .formatted(timeRange, serverIdentifier, itemIdentifier)));

        return itemPriceMapper.toPageResponse(page);
    }


    public ItemPricePageResponse getRecentForItemListAndServers(@Valid ItemPriceRequest request, Pageable pageable) {
        requireNonNull(request, "Item price request cannot be null");
        requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);

        Page<ItemPrice> page = findRecentForRequest(request, pageable);
        notEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No recent prices found for item list"));

        return itemPriceMapper.toPageResponse(page);
    }

    @Transactional
    public void saveAll(List<ItemPrice> itemPricesToSave) {
        requireNonNull(itemPricesToSave, "Item prices cannot be null");

        itemPriceRepository.saveAll(itemPricesToSave, jdbcTemplate);
    }

    private Page<ItemPrice> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<ItemPrice> specification = SpecificationBuilder.from(searchRequest);
        return itemPriceRepository.findAll(specification, pageable);
    }


    private Page<ItemPrice> findRecentForServer(String serverIdentifier, Pageable pageable) {
        int serverId = serverService.getServer(serverIdentifier).id();
        return itemPriceRepository.findRecentForServer(serverId, pageable);
    }


    private Page<ItemPrice> findForServerAndTimeRange(String serverIdentifier,
                                                      String itemIdentifier,
                                                      TimeRange timeRange,
                                                      Pageable pageable) {
        int itemId = itemService.getItem(itemIdentifier).id();
        int serverId = serverService.getServer(serverIdentifier).id();

        return itemPriceRepository.findForServerAndTimeRange(serverId, itemId, timeRange.start(), timeRange.end(), pageable);
    }


    private List<ItemPrice> findRecentForServerAndItem(String serverIdentifier, String itemIdentifier) {
        int itemId = itemService.getItem(itemIdentifier).id();
        int serverId = serverService.getServer(serverIdentifier).id();

        return itemPriceRepository.findRecentForServerAndItem(serverId, itemId);
    }


    private List<ItemPrice> findRecentForRegionAndItem(String regionName, String itemIdentifier) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        int itemId = itemService.getItem(itemIdentifier).id();

        return itemPriceRepository.findRecentForRegionAndItem(region.ordinal(), itemId);
    }


    private List<ItemPrice> findRecentForFactionAndItem(String factionName, String itemIdentifier) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        int itemId = itemService.getItem(itemIdentifier).id();

        return itemPriceRepository.findRecentForFactionAndItem(faction.ordinal(), itemId);
    }


    private Page<ItemPrice> findRecentForRequest(ItemPriceRequest request, Pageable pageable) {
        Set<Integer> itemIds = getItemIds(request.itemList());
        if (isCollectionEmpty(request.serverList())) {
            return itemPriceRepository.findRecentForItemList(itemIds, pageable);
        }
        Set<Integer> serverIds = getServerIds(request.serverList());

        return itemPriceRepository.findRecentForItemListAndServers(itemIds, serverIds, pageable);
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
