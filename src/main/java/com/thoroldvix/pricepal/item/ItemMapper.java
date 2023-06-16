package com.thoroldvix.pricepal.item;


import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    ItemResponse toItemResponse(Item item);

    List<ItemResponse> toItemResponseList(List<Item> items);
}
