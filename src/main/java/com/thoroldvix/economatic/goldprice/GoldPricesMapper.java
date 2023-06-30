package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.shared.Filters;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.thoroldvix.economatic.shared.ErrorMessages.PAGE_CANNOT_BE_NULL;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class GoldPricesMapper {
    private static final GoldPriceMapper GOLD_PRICE_MAPPER = Mappers.getMapper(GoldPriceMapper.class);
    private static final String PRICES_CANNOT_BE_NULL_OR_EMPTY = "Prices cannot be null or empty";

    public GoldPricesPagedResponse toPagedWithServer(@NotNull(message = PAGE_CANNOT_BE_NULL) Page<GoldPrice> page) {
        return createPagedPricesResponse(page, true);
    }

    public GoldPricesPagedResponse toPaged(@NotNull(message = PAGE_CANNOT_BE_NULL) Page<GoldPrice> page) {
        return createPagedPricesResponse(page, false);
    }

    public GoldPricesResponse toResponse(@NotEmpty(message = PRICES_CANNOT_BE_NULL_OR_EMPTY) List<GoldPrice> prices) {
        return makeGoldPricesResponse(null, GOLD_PRICE_MAPPER.toResponseListWithServer(prices));
    }

    public GoldPricesResponse toRegionResponse(@NotEmpty(message = PRICES_CANNOT_BE_NULL_OR_EMPTY) List<GoldPrice> prices) {
        String region = prices.get(0).getServer().getRegion().toString();
        return makeGoldPricesResponse(Filters.builder().region(region).build(), GOLD_PRICE_MAPPER.toResponseListWithServer(prices));
    }

    public GoldPricesResponse toFactionResponse(@NotEmpty(message = PRICES_CANNOT_BE_NULL_OR_EMPTY) List<GoldPrice> prices) {
        String faction = prices.get(0).getServer().getFaction().toString();
        return makeGoldPricesResponse(Filters.builder().faction(faction).build(), GOLD_PRICE_MAPPER.toResponseListWithServer(prices));
    }

    public GoldPricesResponse toServerResponse(@NotEmpty(message = PRICES_CANNOT_BE_NULL_OR_EMPTY) List<GoldPrice> prices) {
        String server = prices.get(0).getServer().getUniqueName();
        return makeGoldPricesResponse(Filters.builder().server(server).build(), GOLD_PRICE_MAPPER.toResponseList(prices));
    }

    private GoldPricesResponse makeGoldPricesResponse(Filters filters, List<GoldPriceResponse> priceResponses) {
        return GoldPricesResponse.builder()
                .filters(filters)
                .prices(priceResponses)
                .build();
    }

    private GoldPricesPagedResponse createPagedPricesResponse(Page<GoldPrice> page, boolean withServer) {
        GoldPricesResponse goldPricesResponse = withServer ? toServerResponse(page.getContent()) : toResponse(page.getContent());

        return GoldPricesPagedResponse.builder()
                .pricesResponse(goldPricesResponse)
                .page(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }
}
