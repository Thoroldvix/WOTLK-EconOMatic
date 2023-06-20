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



    ItemPriceResponse toResponse(ItemPrice itemPrice);

    List<ItemPriceResponse> toResponseList(List<ItemPrice> itemPrice);

    @Named("itemName")
    default String serverName(Server server) {
        return server.getUniqueName();
    }



}
