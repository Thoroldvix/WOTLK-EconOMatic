package com.thoroldvix.economatic.deal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ItemDealsMapper {

    RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    int SCALE = 2;
    BigDecimal DEFAULT_DISCOUNT_PERCENTAGE = BigDecimal.ZERO;

    @Mapping(target = "discountPercentage", qualifiedByName = "mapDiscountPercentage")
    @Mapping(target = "server", source = "uniqueServerName")
    ItemDealResponse toResponse(ItemDealProjection deal);

    List<ItemDealResponse> toResponseList(List<ItemDealProjection> deals);

    @Named("mapDiscountPercentage")
    default BigDecimal mapDiscountPercentage(BigDecimal discountPercentage) {
        return Objects.requireNonNullElse(discountPercentage, DEFAULT_DISCOUNT_PERCENTAGE)
                .setScale(SCALE, ROUNDING_MODE);
    }

    default ItemDealsList toItemDealsList(List<ItemDealProjection> deals) {
        return new ItemDealsList(toResponseList(deals));
    }
}
