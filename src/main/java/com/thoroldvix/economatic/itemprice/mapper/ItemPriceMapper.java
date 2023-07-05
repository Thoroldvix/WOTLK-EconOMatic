package com.thoroldvix.economatic.itemprice.mapper;

import com.thoroldvix.economatic.item.model.Item;
import com.thoroldvix.economatic.itemprice.dto.ItemPriceListResponse;
import com.thoroldvix.economatic.itemprice.dto.ItemPricePageResponse;
import com.thoroldvix.economatic.itemprice.dto.ItemPriceResponse;
import com.thoroldvix.economatic.itemprice.model.ItemPrice;
import com.thoroldvix.economatic.server.model.Server;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.thoroldvix.economatic.shared.util.ValidationUtils.checkNullAndGet;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemPriceMapper {

    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    @Mapping(target = "itemId", source = "item", qualifiedByName = "id")
    @Mapping(target = "itemName", source = "item", qualifiedByName = "itemName")
    ItemPriceResponse toResponse(ItemPrice itemPrice);


    List<ItemPriceResponse> toList(List<ItemPrice> prices);


    default ItemPriceListResponse toItemPriceList(List<ItemPrice> prices) {
        return new ItemPriceListResponse(toList(prices));
    }

    default ItemPricePageResponse toPageResponse(Page<ItemPrice> page) {
        List<ItemPriceResponse> prices = toList(page.getContent());
        return new ItemPricePageResponse(new PaginationInfo(page), prices);
    }

    @Named("serverName")
    default String serverName(Server server) {
       return checkNullAndGet(server::getUniqueName);
    }

    @Named("itemName")
    default String itemName(Item item) {
        return checkNullAndGet(item::getUniqueName);
    }

    @Named("id")
    default Integer itemId(Item item) {
       return checkNullAndGet(item::getId);
    }
}
