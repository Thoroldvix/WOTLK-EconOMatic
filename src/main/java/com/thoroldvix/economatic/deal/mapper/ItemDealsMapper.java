package com.thoroldvix.economatic.deal.mapper;

import com.thoroldvix.economatic.deal.dto.ItemDealResponse;
import com.thoroldvix.economatic.deal.dto.ItemDealsList;
import com.thoroldvix.economatic.deal.repository.ItemDealProjection;
import org.mapstruct.*;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Validated
public interface ItemDealsMapper {

    @Mapping(target = "discountPercentage", qualifiedByName = "discountPercentage")
    @Mapping(target = "server", ignore = true)
    ItemDealResponse toResponse(ItemDealProjection deal);

    List<ItemDealResponse> toResponseList(List<ItemDealProjection> deals);

    @Named("discountPercentage")
    default BigDecimal discountPercentage(BigDecimal discountPercentage) {
        Objects.requireNonNull(discountPercentage, "Discount percentage cannot be null");
        return discountPercentage.setScale(2, RoundingMode.HALF_UP);
    }
    
    default ItemDealsList toItemDealsList(List<ItemDealProjection> deals) {
        return new ItemDealsList(toResponseList(deals));
    }
}
