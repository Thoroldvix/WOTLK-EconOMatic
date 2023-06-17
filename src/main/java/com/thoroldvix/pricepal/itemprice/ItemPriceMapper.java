package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.item.ItemMapper;
import com.thoroldvix.pricepal.server.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ItemMapper.class})
public interface ItemPriceMapper {

    @Mapping(target = "itemInfo", source = "item")
    @Mapping(target = "server", source = "server", qualifiedByName = "name")
    ItemPriceResponse toResponse(ItemPrice itemPrice);

    List<ItemPriceResponse> toResponseList(List<ItemPrice> itemPrice);

    @Named("name")
    default String serverName(Server server) {
        return server.getUniqueName();
    }



}
