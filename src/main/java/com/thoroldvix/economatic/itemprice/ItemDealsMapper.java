package com.thoroldvix.economatic.itemprice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemDealsMapper {


    @Mapping(target = "discountPercentage", source = "discountPercentage", qualifiedByName = "discountPercentage")
    @Mapping(target = "server", ignore = true)
    ItemDealResponse toResponse(ItemDealProjection deal);


    default List<ItemDealResponse> toResponseList(List<ItemDealProjection> deals) {
        return deals.stream().map(this::toResponse).toList();
    }


    default ItemDealsResponse toDealsWithServer(List<ItemDealProjection> dealsForServer) {
        return ItemDealsResponse.builder()
                .server(dealsForServer.get(0).getUniqueServerName())
                .deals(toResponseList(dealsForServer))
                .build();
    }


    @Named("discountPercentage")
    default BigDecimal discountPercentage(BigDecimal discountPercentage) {
        return discountPercentage.setScale(2, RoundingMode.HALF_UP);
    }
}
