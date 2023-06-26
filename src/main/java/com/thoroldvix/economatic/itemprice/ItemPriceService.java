package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.item.ItemService;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.shared.SearchRequest;
import com.thoroldvix.economatic.shared.SearchSpecification;
import com.thoroldvix.economatic.shared.StringEnumConverter;
import com.thoroldvix.economatic.shared.TimeRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.thoroldvix.economatic.server.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.ErrorMessages.PAGEABLE_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.shared.ErrorMessages.SEARCH_REQUEST_CANNOT_BE_NULL;
import static com.thoroldvix.economatic.shared.ValidationUtils.validateCollectionNotNullOrEmpty;
import static com.thoroldvix.economatic.shared.ValidationUtils.validateStringNonNullOrEmpty;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemPriceService {

    public static final String ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY = "Item identifier cannot be null or empty";
    private final ItemService itemServiceImpl;
    private final ServerService serverServiceImpl;
    private final ItemPriceRepository itemPriceRepository;
    private final ItemPriceMapper itemPriceMapper;
    private final AuctionHouseMapper auctionHouseMapper;
    private final SearchSpecification<ItemPrice> searchSpecification;
    private final JdbcTemplate jdbcTemplate;


    public PagedAuctionHouseInfo getRecentForServer(String serverIdentifier, Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        Page<ItemPrice> page = findRecentForServer(serverIdentifier, pageable);

        validateCollectionNotNullOrEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException("No recent item prices found for server identifier " + serverIdentifier));

        return auctionHouseMapper.toPagedWithServer(page);
    }


    public AuctionHouseInfo getRecentForRegion(String regionName, String itemIdentifier) {
        validateStringNonNullOrEmpty(regionName, REGION_NAME_CANNOT_BE_NULL_OR_EMPTY);
        validateStringNonNullOrEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        List<ItemPrice> itemPrices = findRecentForRegionAndItem(regionName, itemIdentifier);
        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for region and item identifier " + regionName + " " + itemIdentifier));

        return auctionHouseMapper.toInfoWithRegionAndItem(itemPrices);
    }


    public AuctionHouseInfo getRecentForFaction(String factionName, String itemIdentifier) {
        validateStringNonNullOrEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        validateStringNonNullOrEmpty(factionName, FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY);

        List<ItemPrice> itemPrices = findRecentForFactionAndItem(factionName, itemIdentifier);
        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for faction and item identifier " + factionName + " " + itemIdentifier));

        return auctionHouseMapper.toInfoWithFactionAndItem(itemPrices);
    }


    public AuctionHouseInfo getRecentForServer(String serverIdentifier, String itemIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        validateStringNonNullOrEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);

        List<ItemPrice> itemPrices = findRecentForServerAndItem(serverIdentifier, itemIdentifier);

        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException(String.format("No item prices found for server identifier %s and item identifier %s", serverIdentifier, itemIdentifier)));

        return auctionHouseMapper.toInfoWithServerAndItem(itemPrices);
    }


    public ItemPricePagedResponse search(SearchRequest searchRequest, Pageable pageable) {
        Objects.requireNonNull(searchRequest, SEARCH_REQUEST_CANNOT_BE_NULL);
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);

        Page<ItemPrice> page = findAllForSearch(searchRequest, pageable);
        validateCollectionNotNullOrEmpty(page.getContent(), () -> new ItemPriceNotFoundException("No item prices found for search request"));

        return itemPriceMapper.toPagedResponse(page);
    }


    public PagedAuctionHouseInfo getForServer(String serverIdentifier, String itemIdentifier, TimeRange timeRange, Pageable pageable) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        validateStringNonNullOrEmpty(itemIdentifier, ITEM_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        Page<ItemPrice> page = findForServerAndTimeRange(serverIdentifier, itemIdentifier, timeRange, pageable);

        validateCollectionNotNullOrEmpty(page.getContent(),
                () -> new ItemPriceNotFoundException(
                        String.format("No item prices found for time range %s for server identifier %s and item identifier %s",
                                timeRange, serverIdentifier, itemIdentifier)));

        return auctionHouseMapper.toPagedWithServerAndItem(page);
    }


    @Transactional
    public void saveAll(List<ItemPrice> itemPricesToSave) {
        Objects.requireNonNull(itemPricesToSave, "Item prices cannot be null");
        itemPriceRepository.saveAll(itemPricesToSave, jdbcTemplate);
    }

    private Page<ItemPrice> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<ItemPrice> specification = searchSpecification.create(searchRequest.globalOperator(), searchRequest.searchCriteria());
        return itemPriceRepository.findAll(specification, pageable);
    }


    private Page<ItemPrice> findRecentForServer(String serverIdentifier, Pageable pageable) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return itemPriceRepository.findRecentForServer(serverId, pageable);
    }


    private Page<ItemPrice> findForServerAndTimeRange(String serverIdentifier,
                                                      String itemIdentifier,
                                                      TimeRange timeRange,
                                                      Pageable pageable) {
        int itemId = itemServiceImpl.getItem(itemIdentifier).id();
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return itemPriceRepository.findForServerAndTimeRange(serverId, itemId, timeRange.start(), timeRange.end(), pageable);
    }


    private List<ItemPrice> findRecentForServerAndItem(String serverIdentifier, String itemIdentifier) {
        int itemId = itemServiceImpl.getItem(itemIdentifier).id();
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return itemPriceRepository.findRecentForServerAndItem(serverId, itemId);
    }


    private List<ItemPrice> findRecentForRegionAndItem(String regionName, String itemIdentifier) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        int itemId = itemServiceImpl.getItem(itemIdentifier).id();
        return itemPriceRepository.findRecentForRegionAndItem(region.ordinal(), itemId);
    }


    private List<ItemPrice> findRecentForFactionAndItem(String factionName, String itemIdentifier) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        int itemId = itemServiceImpl.getItem(itemIdentifier).id();
        return itemPriceRepository.findRecentForFactionAndItem(faction.ordinal(), itemId);
    }
}
