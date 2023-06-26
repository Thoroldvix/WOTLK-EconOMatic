package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.shared.ErrorMessages;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuctionHouseMapper {

    ItemPriceMapper itemPriceMapper = Mappers.getMapper(ItemPriceMapper.class);


    default PagedAuctionHouseInfo toPagedWithServer(Page<ItemPrice> page) {
        Objects.requireNonNull(page, ErrorMessages.PAGE_CANNOT_BE_NULL);
        AuctionHouseInfo auctionHouseInfo = getInfoWithServer(page);

        return PagedAuctionHouseInfo.builder()
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .page(page.getNumber())
                .auctionHouseInfo(auctionHouseInfo)
                .build();
    }


    default AuctionHouseInfo toInfoWithRegionAndItem(List<ItemPrice> itemPrices) {
        String region = itemPrices.get(0).getServer().getRegion().toString();
        int itemId = itemPrices.get(0).getItem().getId();
        String itemName = itemPrices.get(0).getItem().getName();
        List<ItemPriceResponse> prices = itemPriceMapper.toResponseWithServerList(itemPrices);

        return AuctionHouseInfo.builder()
                .itemId(itemId)
                .itemName(itemName)
                .prices(prices)
                .region(region)
                .build();
    }


    default AuctionHouseInfo toInfoWithFactionAndItem(List<ItemPrice> itemPrices) {
        String faction = itemPrices.get(0).getServer().getFaction().toString();
        int itemId = itemPrices.get(0).getItem().getId();
        String itemName = itemPrices.get(0).getItem().getName();
        List<ItemPriceResponse> prices = itemPriceMapper.toResponseWithServerList(itemPrices);

        return AuctionHouseInfo.builder()
                .itemName(itemName)
                .itemId(itemId)
                .prices(prices)
                .faction(faction)
                .build();
    }


    default AuctionHouseInfo toInfoWithServerAndItem(List<ItemPrice> itemPrices) {
        String serverName = itemPrices.get(0).getServer().getUniqueName();
        int itemId = itemPrices.get(0).getItem().getId();
        String itemName = itemPrices.get(0).getItem().getName();
        List<ItemPriceResponse> prices = itemPriceMapper.toResponseList(itemPrices);

        return AuctionHouseInfo.builder()
                .itemId(itemId)
                .itemName(itemName)
                .prices(prices)
                .server(serverName)
                .build();
    }


    default PagedAuctionHouseInfo toPagedWithServerAndItem(Page<ItemPrice> page) {
        AuctionHouseInfo auctionHouseInfo = getInfoWithServerAndItem(page);

        return PagedAuctionHouseInfo.builder()
                .page(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .auctionHouseInfo(auctionHouseInfo)
                .build();
    }

    private AuctionHouseInfo getInfoWithServer(Page<ItemPrice> page) {
        List<ItemPriceResponse> prices = itemPriceMapper.toResponseWithItemList(page.getContent());
        String serverName = page.getContent().get(0).getServer().getUniqueName();


        return AuctionHouseInfo.builder()
                .server(serverName)
                .prices(prices)
                .build();
    }


    private AuctionHouseInfo getInfoWithServerAndItem(Page<ItemPrice> page) {
        List<ItemPriceResponse> prices = itemPriceMapper.toResponseList(page.getContent());
        String serverName = page.getContent().get(0).getServer().getUniqueName();
        int itemId = page.getContent().get(0).getItem().getId();
        String itemName = page.getContent().get(0).getItem().getName();

        return AuctionHouseInfo.builder()
                .server(serverName)
                .itemId(itemId)
                .itemName(itemName)
                .prices(prices)
                .build();
    }
}
