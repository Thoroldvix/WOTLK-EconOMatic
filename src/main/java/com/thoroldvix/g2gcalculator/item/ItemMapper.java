package com.thoroldvix.g2gcalculator.item;


import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {


    ItemInfo toItemInfo(Item item);
}
