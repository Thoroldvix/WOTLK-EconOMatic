package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.deal.dto.ItemDealResponse;
import com.thoroldvix.economatic.deal.dto.ItemDealsList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemDealsMapper {

    @Mapping(target = "discountPercentage", qualifiedByName = "mapDiscountPercentage")
    @Mapping(target = "server", source = "uniqueServerName")
    ItemDealResponse toResponse(ItemDealProjection deal);

    List<ItemDealResponse> toResponseList(List<ItemDealProjection> deals);

    @Named("mapDiscountPercentage")
    default BigDecimal mapDiscountPercentage(BigDecimal discountPercentage) {
        return Objects.requireNonNullElse(discountPercentage, BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    default ItemDealsList toItemDealsList(List<ItemDealProjection> deals) {
        return new ItemDealsList(toResponseList(deals));
    }
}
