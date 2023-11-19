package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.common.dto.TimeRange;
import com.thoroldvix.economatic.common.util.StringEnumConverter;
import com.thoroldvix.economatic.item.ItemResponse;
import com.thoroldvix.economatic.item.ItemService;
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

import static com.thoroldvix.economatic.common.util.ValidationUtils.isCollectionEmpty;
import static com.thoroldvix.economatic.common.util.ValidationUtils.notEmpty;
import static com.thoroldvix.economatic.error.ErrorMessages.*;

@Service
@Cacheable("item-price-cache")
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemPriceServiceImpl implements ItemPriceService {

    private final ItemService itemService;
    private final ServerService serverService;
    private final ItemPriceRepository itemPriceRepository;
    private final ItemPriceMapper itemPriceMapper;
    private final ItemPriceJdbcRepository jdbcRepository;

    @Override
    public ItemPricePageResponse getRecentForServer(String serverIdentifier, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        ServerResponse server = serverService.getServer(serverIdentifier);
        Page<ItemPrice> page = itemPriceRepository.findRecentForServer(server.id(), pageable);

        notEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No recent item prices found for server identifier " + serverIdentifier));

        return itemPriceMapper.toPageResponse(page);
    }

    @Override
    public ItemPriceListResponse getRecentForRegion(String regionName, String itemIdentifier) {
        notEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        ItemResponse item = itemService.getItem(itemIdentifier);
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        List<ItemPrice> itemPrices = itemPriceRepository.findRecentForRegionAndItem(region.ordinal(), item.id());

        notEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for region and item identifier " + regionName + " " + itemIdentifier));

        return itemPriceMapper.toItemPriceList(itemPrices);
    }

    @Override
    public ItemPriceListResponse getRecentForFaction(String factionName, String itemIdentifier) {
        notEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY.message);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        ItemResponse item = itemService.getItem(itemIdentifier);
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        List<ItemPrice> itemPrices = itemPriceRepository.findRecentForFactionAndItem(faction.ordinal(), item.id());

        notEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for faction and item identifier " + factionName + " " + itemIdentifier));

        return itemPriceMapper.toItemPriceList(itemPrices);
    }

    @Override
    public ItemPriceListResponse getRecentForServer(String serverIdentifier, String itemIdentifier) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        ServerResponse server = serverService.getServer(serverIdentifier);
        ItemResponse item = itemService.getItem(itemIdentifier);
        List<ItemPrice> itemPrices = itemPriceRepository.findRecentForServerAndItem(server.id(), item.id());

        notEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for server identifier %s and item identifier %s"
                        .formatted(serverIdentifier, itemIdentifier)));

        return itemPriceMapper.toItemPriceList(itemPrices);
    }

    @Override
    public ItemPricePageResponse search(@Valid SearchRequest searchRequest, Pageable pageable) {
        Specification<ItemPrice> specification = SpecificationBuilder.from(searchRequest);
        Page<ItemPrice> page = itemPriceRepository.findAll(specification, pageable);
        notEmpty(page.getContent(), () -> new ItemPriceNotFoundException("No item prices found for search request"));

        return itemPriceMapper.toPageResponse(page);
    }

    @Override
    public ItemPricePageResponse getForServer(String serverIdentifier, String itemIdentifier, TimeRange timeRange, Pageable pageable) {
        notEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);
        notEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY.message);

        ServerResponse server = serverService.getServer(serverIdentifier);
        ItemResponse item = itemService.getItem(itemIdentifier);

        Page<ItemPrice> page = itemPriceRepository.findForServerAndTimeRange(
                server.id(),
                item.id(),
                timeRange.start(),
                timeRange.end(),
                pageable
        );

        notEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No item prices found for time range %s for server identifier %s and item identifier %s"
                        .formatted(timeRange, serverIdentifier, itemIdentifier)));

        return itemPriceMapper.toPageResponse(page);
    }

    @Override
    public ItemPricePageResponse getRecentForItemListAndServers(@Valid ItemPriceRequest request, Pageable pageable) {
        Set<Integer> itemIds = getItemIds(request.itemList());
        Set<Integer> serverIds = getServerIds(request.serverList());
        Page<ItemPrice> page = findRecentForItemsAndServers(serverIds, itemIds, pageable);

        notEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No recent prices found for item list"));

        return itemPriceMapper.toPageResponse(page);
    }

    private Set<Integer> getItemIds(Set<String> itemList) {
        return itemList.parallelStream()
                .map(item -> itemService.getItem(item).id())
                .collect(Collectors.toSet());
    }

    private Set<Integer> getServerIds(Set<String> serverList) {
        return serverList.parallelStream()
                .map(server -> serverService.getServer(server).id())
                .collect(Collectors.toSet());
    }

    private Page<ItemPrice> findRecentForItemsAndServers(Set<Integer> serverIds, Set<Integer> itemIds, Pageable pageable) {
        return isCollectionEmpty(serverIds)
                ? itemPriceRepository.findRecentForItemList(itemIds, pageable)
                : itemPriceRepository.findRecentForItemsAndServers(itemIds, serverIds, pageable);
    }

    @Override
    @Transactional
    public void saveAll(List<ItemPrice> itemPricesToSave) {
        jdbcRepository.saveAll(itemPricesToSave);
    }

}
