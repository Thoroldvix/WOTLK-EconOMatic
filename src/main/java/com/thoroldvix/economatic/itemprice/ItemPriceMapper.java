package com.thoroldvix.economatic.itemprice;

import com.thoroldvix.economatic.item.Item;
import com.thoroldvix.economatic.item.ItemMapper;
import com.thoroldvix.economatic.server.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ItemMapper.class})
@Validated
public interface ItemPriceMapper {

    String ITEM_PRICE_CANNOT_BE_NULL = "Item prices cannot be null";
    String ITEM_CANNOT_BE_NULL = "Item cannot be null";


    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "server", ignore = true)
    @Mapping(target = "itemName", ignore = true)
    ItemPriceResponse toResponse(

            ItemPrice itemPrice);


    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    @Mapping(target = "itemId", source = "item", qualifiedByName = "id")
    @Mapping(target = "itemName", source = "item", qualifiedByName = "itemName")
    ItemPriceResponse toFullResponse(ItemPrice itemPrice);


    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "itemName", ignore = true)
    ItemPriceResponse toResponseWithServer(ItemPrice itemPrice);


    @Mapping(target = "server", ignore = true)
    @Mapping(target = "itemId", source = "item", qualifiedByName = "id")
    @Mapping(target = "itemName", source = "item", qualifiedByName = "itemName")
    ItemPriceResponse toResponseWithItem(ItemPrice itemPrice);


    default List<ItemPriceResponse> toResponseList(List<ItemPrice> itemPrice) {
        return itemPrice.stream().map(this::toResponse).toList();
    }


    default List<ItemPriceResponse> toResponseListWithServer(List<ItemPrice> itemPrice) {
        Objects.requireNonNull(itemPrice, ITEM_PRICE_CANNOT_BE_NULL);
        return itemPrice.stream().map(this::toResponseWithServer).toList();
    }


    default List<ItemPriceResponse> toResponseListWithItem(List<ItemPrice> itemPrice) {
        Objects.requireNonNull(itemPrice, ITEM_PRICE_CANNOT_BE_NULL);
        return itemPrice.stream().map(this::toResponseWithItem).toList();
    }


    default List<ItemPriceResponse> toFullResponseList(List<ItemPrice> itemPrice) {
        Objects.requireNonNull(itemPrice, ITEM_PRICE_CANNOT_BE_NULL);
        return itemPrice.stream().map(this::toFullResponse).toList();
    }


    default ItemPricePagedResponse toPagedResponse(Page<ItemPrice> page) {
        List<ItemPriceResponse> prices = toFullResponseList(page.getContent());
        return ItemPricePagedResponse.builder()
                .prices(prices)
                .page(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }


    @Named("serverName")
    default String serverName(Server server) {
        Objects.requireNonNull(server, "Server cannot be null");
        return server.getUniqueName();
    }


    @Named("itemName")
    default String itemName(Item item) {
        Objects.requireNonNull(item, ITEM_CANNOT_BE_NULL);
        return item.getName();
    }


    @Named("id")
    default int itemId(Item item) {
        Objects.requireNonNull(item, ITEM_CANNOT_BE_NULL);
        return item.getId();
    }
}
