package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.shared.Filters;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.thoroldvix.economatic.shared.ErrorMessages.PAGE_CANNOT_BE_NULL;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Validated
public abstract class AuctionHouseMapper {

    private static final ItemPriceMapper ITEM_PRICE_MAPPER = Mappers.getMapper(ItemPriceMapper.class);
    private static final String ITEM_PRICES_CANNOT_BE_NULL_OR_EMPTY = "Item prices cannot be null or empty";

    private static AuctionHouseInfo makeAuctionHouseInfo(Filters filters, List<ItemPriceResponse> prices, Integer itemId, String itemName) {
        AuctionHouseInfo.AuctionHouseFilters auctionHouseFilters = createAuctionHouseFilters(filters, itemId, itemName);
        return AuctionHouseInfo.builder()
                .filters(auctionHouseFilters)
                .prices(prices)
                .build();
    }

    private static AuctionHouseInfo.AuctionHouseFilters createAuctionHouseFilters(Filters filters, Integer itemId, String itemName) {
        return AuctionHouseInfo.AuctionHouseFilters.builder()
                .filters(filters)
                .itemId(itemId)
                .itemName(itemName)
                .build();
    }

    private PagedAuctionHouseInfo createPagedAuctionHouseInfo(Page<ItemPrice> page, boolean withItem) {
        AuctionHouseInfo auctionHouseInfo = withItem ? toInfoWithServerAndItem(page.getContent()) : toInfoWithServer(page.getContent());

        return PagedAuctionHouseInfo.builder()
                .pageSize(page.getSize())
                .page(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .auctionHouseInfo(auctionHouseInfo)
                .build();
    }

    public PagedAuctionHouseInfo toPagedWithServer(@NotNull(message = PAGE_CANNOT_BE_NULL) Page<ItemPrice> page) {
        return createPagedAuctionHouseInfo(page, false);
    }

    public PagedAuctionHouseInfo toPagedWithServerAndItem(@NotNull(message = PAGE_CANNOT_BE_NULL) Page<ItemPrice> page) {
        return createPagedAuctionHouseInfo(page, true);
    }

    public AuctionHouseInfo toInfoWithRegionAndItem(@NotEmpty(message = ITEM_PRICES_CANNOT_BE_NULL_OR_EMPTY) List<ItemPrice> itemPrices) {
        String region = itemPrices.get(0).getServer().getRegion().toString();
        Filters regionFilter = Filters.builder().region(region).build();
        Integer itemId = itemPrices.get(0).getItem().getId();
        String itemName = itemPrices.get(0).getItem().getName();

        return makeAuctionHouseInfo(regionFilter, ITEM_PRICE_MAPPER.toResponseListWithServer(itemPrices), itemId, itemName);
    }

    public AuctionHouseInfo toInfoWithFactionAndItem(@NotEmpty(message = ITEM_PRICES_CANNOT_BE_NULL_OR_EMPTY) List<ItemPrice> itemPrices) {
        String faction = itemPrices.get(0).getServer().getFaction().toString();
        Filters factionFilter = Filters.builder().faction(faction).build();
        Integer itemId = itemPrices.get(0).getItem().getId();
        String itemName = itemPrices.get(0).getItem().getName();

        return makeAuctionHouseInfo(factionFilter, ITEM_PRICE_MAPPER.toResponseListWithServer(itemPrices), itemId, itemName);
    }

    public AuctionHouseInfo toInfoWithServer(@NotEmpty(message = ITEM_PRICES_CANNOT_BE_NULL_OR_EMPTY) List<ItemPrice> itemPrices) {
        String server = itemPrices.get(0).getServer().getUniqueName();
        Filters serverFilter = Filters.builder().server(server).build();

        return makeAuctionHouseInfo(serverFilter, ITEM_PRICE_MAPPER.toResponseListWithItem(itemPrices), null, null);
    }

    public AuctionHouseInfo toInfoWithServerAndItem(@NotEmpty(message = ITEM_PRICES_CANNOT_BE_NULL_OR_EMPTY) List<ItemPrice> itemPrices) {
        String server = itemPrices.get(0).getServer().getUniqueName();
        Filters serverFilter = Filters.builder().server(server).build();
        Integer itemId = itemPrices.get(0).getItem().getId();
        String itemName = itemPrices.get(0).getItem().getName();

        return makeAuctionHouseInfo(serverFilter, ITEM_PRICE_MAPPER.toResponseList(itemPrices), itemId, itemName);
    }


}