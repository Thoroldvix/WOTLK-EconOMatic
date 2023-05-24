package com.thoroldvix.pricepal.item.service;


import com.thoroldvix.pricepal.item.dto.ItemInfo;
import com.thoroldvix.pricepal.item.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(target = "icon", source = "icon", qualifiedByName = "getIconUrl")
    ItemInfo toItemInfo(Item item);

    @Named("getIconUrl")
    default String getIconUrl (String iconName) {
        return String.format("https://wow.zamimg.com/images/wow/icons/large/%s.jpg", iconName);
    }
}
