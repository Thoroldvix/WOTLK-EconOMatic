package com.thoroldvix.pricepal.item;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(target = "iconLink", source = "iconLink")
    ItemResponse toItemResponse(Item item);


    List<ItemResponse> toitemResponseList(List<Item> items);
}
