package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.item.ItemService;
import com.thoroldvix.pricepal.server.Region;
import com.thoroldvix.pricepal.server.ServerService;
import com.thoroldvix.pricepal.shared.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.thoroldvix.pricepal.shared.ValidationUtils.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemPriceService {

    private final ItemService itemServiceImpl;
    private final ServerService serverServiceImpl;
    private final ItemPriceRepository itemPriceRepository;
    private final ItemPriceMapper itemPriceMapper;
    private final SearchSpecification<ItemPrice> searchSpecification;
    private final JdbcTemplate jdbcTemplate;

    public AuctionHouseInfo getRecentForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier must not be null or empty");
        List<ItemPrice> itemPrices = findRecentForServer(serverIdentifier);
        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for server identifier " + serverIdentifier));

        return createAuctionHouseInfo(itemPrices);
    }

    public AuctionHouseInfo getRecentForRegion(String regionName, String itemIdentifier) {
        List<ItemPrice> itemPrices = findRecentForRegionAndItem(regionName, itemIdentifier);
        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for region and item identifier " + regionName + " " + itemIdentifier));

        return createAuctionHouseInfo(itemPrices);
    }

    private List<ItemPrice> findRecentForRegionAndItem(String regionName, String itemIdentifier) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return null;
    }

    public AuctionHouseInfo getRecentForServer(String serverIdentifier, String itemIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        validateStringNonNullOrEmpty(itemIdentifier, "Item identifier cannot be null or empty");

        List<ItemPrice> itemPrices = findRecentForServerAndItem(serverIdentifier, itemIdentifier);

        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException(String.format("No item prices found for server identifier %s and item identifier %s", serverIdentifier, itemIdentifier)));

        return createAuctionHouseInfo(itemPrices, itemPrices.get(0));
    }

    public AuctionHouseInfo search(SearchRequest searchRequest, Pageable pageable) {
        Objects.requireNonNull(searchRequest, "Search request cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        List<ItemPrice> itemPrices = findAllForSearch(searchRequest, pageable);
        validateCollectionNotNullOrEmpty(itemPrices, () -> new ItemPriceNotFoundException("No item prices found for search request"));

        return createAuctionHouseInfo(itemPrices, itemPrices.get(0));
    }

    public AuctionHouseInfo getForServer(String serverIdentifier, String itemIdentifier, int timeRange, Pageable pageable) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        validateStringNonNullOrEmpty(itemIdentifier, "Item identifier cannot be null or empty");
        validatePositiveInt(timeRange, "Time range must be a positive integer");
        Objects.requireNonNull(pageable, "Pageable cannot be null");


        List<ItemPrice> itemPrices = findForServerAndTimeRange(serverIdentifier, itemIdentifier, timeRange, pageable);

        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException(
                        String.format("No item prices found for time range %s for server identifier %s and item identifier %s",
                                timeRange, serverIdentifier, itemIdentifier)));

        return createAuctionHouseInfo(itemPrices);
    }

    @Transactional
    public void saveAll(List<ItemPrice> itemPricesToSave) {
        Objects.requireNonNull(itemPricesToSave, "Item prices cannot be null");
        itemPriceRepository.saveAll(itemPricesToSave, jdbcTemplate);
    }

    private AuctionHouseInfo createAuctionHouseInfo(List<ItemPrice> itemPrices) {
        Objects.requireNonNull(itemPrices, "Item price cannot be null");

        List<ItemPriceResponse> items = itemPriceMapper.toResponseList(itemPrices);

        return AuctionHouseInfo.builder()
                .items(items)
                .build();
    }

    private List<ItemPrice> findAllForSearch(SearchRequest searchRequest, Pageable pageable) {
        Specification<ItemPrice> specification = searchSpecification.create(searchRequest.globalOperator(), searchRequest.searchCriteria());
        return itemPriceRepository.findAll(specification, pageable).getContent();
    }

    private List<ItemPrice> findRecentForServer(String serverIdentifier) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return itemPriceRepository.findRecentForServer(serverId);
    }

    private AuctionHouseInfo createAuctionHouseInfo(List<ItemPrice> itemPrices, ItemPrice itemPrice) {
        Objects.requireNonNull(itemPrices, "Item price cannot be null");
        Objects.requireNonNull(itemPrice, "Item price cannot be null");

        List<ItemPriceResponse> items = itemPriceMapper.toResponseList(itemPrices);
        String itemName = itemPrice.getItem().getName();
        int itemId = itemPrice.getItem().getId();
        String serverName = itemPrice.getServer().getUniqueName();

        return AuctionHouseInfo.builder()
                .server(serverName)
                .itemName(itemName)
                .itemId(itemId)
                .items(items)
                .build();
    }

    private List<ItemPrice> findForServerAndTimeRange(String serverIdentifier, String itemIdentifier, int timeRange, Pageable pageable) {
        int itemId = itemServiceImpl.getItem(itemIdentifier).itemId();
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        LocalDateTime start = LocalDateTime.now().minusDays(timeRange);
        LocalDateTime end = LocalDateTime.now();
        return itemPriceRepository.findForServerAndTimeRange(serverId, itemId, start, end, pageable).getContent();
    }

    private List<ItemPrice> findRecentForServerAndItem(String serverIdentifier, String itemIdentifier) {
        int itemId = itemServiceImpl.getItem(itemIdentifier).itemId();
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return itemPriceRepository.findRecentForServerAndItem(serverId, itemId);
    }


}
