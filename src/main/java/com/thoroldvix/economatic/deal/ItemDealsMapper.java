package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.shared.Filters;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Validated
public interface ItemDealsMapper {

    String DEALS_CANNOT_BE_NULL_OR_EMPTY = "Deals cannot be null or empty";

    @Mapping(target = "discountPercentage", qualifiedByName = "discountPercentage")
    @Mapping(target = "server", ignore = true)
    ItemDealResponse toResponse(ItemDealProjection deal);

    List<ItemDealResponse> toResponseList(
            @NotEmpty(message = DEALS_CANNOT_BE_NULL_OR_EMPTY)
            List<ItemDealProjection> deals
    );

    default ItemDealsResponse toDealsWithServer(
            @NotEmpty(message = DEALS_CANNOT_BE_NULL_OR_EMPTY)
            List<ItemDealProjection> dealsForServer
    ) {
        return createItemDealsResponse(dealsForServer);
    }

    @Named("discountPercentage")
    default BigDecimal discountPercentage(
            @NotNull(message = "Discount percentage cannot be null")
            BigDecimal discountPercentage
    ) {
        return discountPercentage.setScale(2, RoundingMode.HALF_UP);
    }


    private ItemDealsResponse createItemDealsResponse(
            List<ItemDealProjection> dealsForServer
    ) {
        String uniqueServerName = dealsForServer.get(0).getUniqueServerName();
        Filters filters = Filters.builder()
                .server(uniqueServerName)
                .build();

        return ItemDealsResponse.builder()
                .filters(filters)
                .deals(toResponseList(dealsForServer))
                .build();
    }
}
