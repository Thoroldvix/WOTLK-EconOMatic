package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.item.ItemService;
import com.thoroldvix.pricepal.shared.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    @Transactional
    public void saveAll(List<ItemPrice> itemPricesToSave) {
        Objects.requireNonNull(itemPricesToSave, "Item prices cannot be null");
        itemPriceRepository.saveAll(itemPricesToSave, jdbcTemplate);
    }

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
        SearchCriteria[] searchCriteria = getSearchCriteria(serverIdentifier, itemIdentifier);
        Specification<ItemPrice> specification = searchSpecification.createSearchSpecification(RequestDto.GlobalOperator.AND, searchCriteria);
        List<ItemPrice> itemPrices = itemPriceRepository.findAll(specification);
        validateCollectionNotNullOrEmpty(itemPrices,
                () -> new ItemPriceNotFoundException(String.format("No item prices found for server identifier %s and item identifier %s", serverIdentifier, itemIdentifier)));

        return createAuctionHouseInfo(itemPrices);
    }

    private SearchCriteria[] getSearchCriteria(String serverIdentifier, String itemIdentifier) {
        SearchCriteria serverCriteria = ServerSearchCriteriaBuilder.getJoinCriteria(serverIdentifier);
        SearchCriteria itemCriteria = getItemCriteria(itemIdentifier);
        return new SearchCriteria[]{serverCriteria, itemCriteria};
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
