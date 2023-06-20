package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.server.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

import static com.thoroldvix.pricepal.shared.ErrorMessages.PAGE_CANNOT_BE_NULL;
import static com.thoroldvix.pricepal.shared.ValidationUtils.validateCollectionNotNullOrEmpty;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GoldPriceMapper {

    String PRICES_CANNOT_BE_NULL_OR_EMPTY = "Prices cannot be null or empty";


    @Mapping(target = "price", source = "value")
    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    GoldPriceResponse toResponseWithServer(GoldPrice goldPrice);

    @Mapping(target = "price", source = "value")
    @Mapping(target = "server", ignore = true)
    GoldPriceResponse toResponse(GoldPrice goldPrice);

    default List<GoldPriceResponse> toResponseListWithServer(List<GoldPrice> prices) {
        validateCollectionNotNullOrEmpty(prices, () -> new IllegalArgumentException(PRICES_CANNOT_BE_NULL_OR_EMPTY));
        return prices.stream().map(this::toResponseWithServer).toList();
    }

    default List<GoldPriceResponse> toResponseList(List<GoldPrice> prices) {
        validateCollectionNotNullOrEmpty(prices, () -> new IllegalArgumentException(PRICES_CANNOT_BE_NULL_OR_EMPTY));
        return prices.stream().map(this::toResponse).toList();
    }

    default GoldPricesPagedResponse toPagedPricesResponse(Page<GoldPrice> page) {
         Objects.requireNonNull(page, PAGE_CANNOT_BE_NULL);
        GoldPricesResponse goldPricesResponse = getGoldPricesResponse(page);
        return GoldPricesPagedResponse.builder()
                .pricesResponse(goldPricesResponse)
                .page(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

    private GoldPricesResponse getGoldPricesResponse(Page<GoldPrice> page) {
        return GoldPricesResponse.builder()
                .prices(toResponseListWithServer(page.getContent()))
                .build();
    }


    default GoldPricesResponse toPricesResponse(List<GoldPrice> prices) {
        validateCollectionNotNullOrEmpty(prices, () -> new IllegalArgumentException(PRICES_CANNOT_BE_NULL_OR_EMPTY));
        return GoldPricesResponse.builder()
                .prices(toResponseListWithServer(prices))
                .build();
    }


    default GoldPricesResponse toPricesRegionResponse(List<GoldPrice> prices) {
        validateCollectionNotNullOrEmpty(prices, () -> new IllegalArgumentException(PRICES_CANNOT_BE_NULL_OR_EMPTY));
        String region = prices.get(0).getServer().getRegion().toString();
        List<GoldPriceResponse> pricesResponse = toResponseListWithServer(prices);
        return GoldPricesResponse.builder()
                .region(region)
                .prices(pricesResponse)
                .build();
    }

    default GoldPricesResponse toPricesFactionResponse(List<GoldPrice> prices) {
        validateCollectionNotNullOrEmpty(prices, () -> new IllegalArgumentException(PRICES_CANNOT_BE_NULL_OR_EMPTY));
        String faction = prices.get(0).getServer().getFaction().toString();
        return GoldPricesResponse.builder()
                .faction(faction)
                .prices(toResponseListWithServer(prices))
                .build();
    }

    @Named("serverName")
    default String serverName(Server server) {
        Objects.requireNonNull(server, "Server cannot be null");
        return server.getUniqueName();
    }


}