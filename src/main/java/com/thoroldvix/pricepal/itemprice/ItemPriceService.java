package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.item.ItemService;
import com.thoroldvix.pricepal.shared.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.thoroldvix.pricepal.shared.ValidationUtils.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemPriceService {

    private final ItemService itemServiceImpl;
    private final ItemPriceRepository itemPriceRepository;
    private final ItemPriceMapper itemPriceMapper;
    private final SearchSpecification<ItemPrice> searchSpecification;
    private final JdbcTemplate jdbcTemplate;


    public AuctionHouseInfo getRecentForServer(String serverIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier must not be null or empty");

        List<ItemPrice> itemPrices = findAllRecentForServer(serverIdentifier);

        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException("No item prices found for server identifier " + serverIdentifier));

        return createAuctionHouseInfo(itemPrices);
    }

    public AuctionHouseInfo getRecentForServer(String serverIdentifier, String itemIdentifier) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        validateStringNonNullOrEmpty(itemIdentifier, "Item identifier cannot be null or empty");

        Specification<ItemPrice> specification = getServerItemSpec(serverIdentifier, itemIdentifier);
        List<ItemPrice> itemPrices = itemPriceRepository.findAll(specification);

        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException(String.format("No item prices found for server identifier %s and item identifier %s", serverIdentifier, itemIdentifier)));

        return createAuctionHouseInfo(itemPrices);
    }

    public AuctionHouseInfo search(SearchRequest searchRequest, Pageable pageable) {
        Objects.requireNonNull(searchRequest, "Search request cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        Specification<ItemPrice> specification = searchSpecification.createSearchSpecification(searchRequest.globalOperator(), searchRequest.searchCriteria());
        List<ItemPrice> itemPrices = itemPriceRepository.findAll(specification, pageable).getContent();
        validateCollectionNotNullOrEmpty(itemPrices, () -> new ItemPriceNotFoundException("No item prices found for search request"));
        return createAuctionHouseInfo(itemPrices);
    }

    public AuctionHouseInfo getForTimeRange(String serverIdentifier, String itemIdentifier, int timeRange, Pageable pageable) {
        validateStringNonNullOrEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        validateStringNonNullOrEmpty(itemIdentifier, "Item identifier cannot be null or empty");
        validatePositiveInt(timeRange, "Time range must be a positive integer");
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        Specification<ItemPrice> timeRangeSpec = searchSpecification.getSpecForTimeRange(timeRange);
        Specification<ItemPrice> combinedSpec = getServerItemSpec(serverIdentifier, itemIdentifier).and(timeRangeSpec);
        List<ItemPrice> itemPrices = itemPriceRepository.findAll(combinedSpec, pageable).getContent();

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

    private SearchCriteria[] getSearchCriteria(String serverIdentifier, String itemIdentifier) {
        SearchCriteria serverCriteria = ServerSearchCriteriaBuilder.getJoinCriteria(serverIdentifier);
        SearchCriteria itemCriteria = getItemCriteria(itemIdentifier);

        return new SearchCriteria[]{serverCriteria, itemCriteria};
    }

     private Specification<ItemPrice> getServerItemSpec(String serverIdentifier, String itemIdentifier) {
        SearchCriteria[] searchCriteria = getSearchCriteria(serverIdentifier, itemIdentifier);

        return searchSpecification.createSearchSpecification(SearchRequest.GlobalOperator.AND, searchCriteria);
    }

    private SearchCriteria getItemCriteria(String itemIdentifier) {
        boolean isNumber = isNumber(itemIdentifier);

        return SearchCriteria.builder()
                .column(isNumber ? "id" : "uniqueName")
                .joinTable("item")
                .operation(SearchCriteria.Operation.EQUALS)
                .value(itemIdentifier)
                .build();
    }

    private AuctionHouseInfo createAuctionHouseInfo(List<ItemPrice> itemPrices) {
        Objects.requireNonNull(itemPrices, "Item price cannot be null");

        List<ItemPriceResponse> items = itemPriceMapper.toResponseList(itemPrices);

        return AuctionHouseInfo.builder()
                .items(items)
                .build();
    }

    private List<ItemPrice> findAllRecentForServer(String serverIdentifier) {
        try {
            int serverId = Integer.parseInt(serverIdentifier);
            return itemPriceRepository.findAllRecentByServerId(serverId);
        } catch (NumberFormatException e) {
            return itemPriceRepository.findAllRecentByUniqueServerName(serverIdentifier);
        }
    }


}
