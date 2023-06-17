package com.thoroldvix.pricepal.item;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(target = "itemId", source = "id")
    ItemResponse toResponse(Item item);

    List<ItemResponse> toResponseList(List<Item> items);
}
