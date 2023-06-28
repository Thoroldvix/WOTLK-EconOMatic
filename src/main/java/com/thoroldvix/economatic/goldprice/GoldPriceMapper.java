package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.server.Server;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.thoroldvix.economatic.shared.ErrorMessages.PAGE_CANNOT_BE_NULL;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Validated
public abstract class GoldPriceMapper {

    private static final String PRICES_CANNOT_BE_NULL_OR_EMPTY = "Prices cannot be null or empty";


    @Mapping(target = "price", source = "value")
    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    abstract GoldPriceResponse toResponseWithServer(@NotNull(message = "Gold price cannot be null")
                                                    GoldPrice goldPrice);

    @Mapping(target = "price", source = "value")
    @Mapping(target = "server", ignore = true)
    abstract GoldPriceResponse toResponse(
            @NotNull(message = "Gold price cannot be null")
            GoldPrice goldPrice);

    public List<GoldPriceResponse> toResponseListWithServer(
            @NotEmpty(message = PRICES_CANNOT_BE_NULL_OR_EMPTY)
            List<GoldPrice> prices) {
        return prices.stream().map(this::toResponseWithServer).toList();
    }

    public List<GoldPriceResponse> toResponseList(
            @NotEmpty(message = PRICES_CANNOT_BE_NULL_OR_EMPTY)
            List<GoldPrice> prices) {
        return prices.stream().map(this::toResponse).toList();
    }

    public GoldPricesPagedResponse toPagedPricesResponse(
            @NotNull(message = PAGE_CANNOT_BE_NULL)
            Page<GoldPrice> page) {

        GoldPricesResponse goldPricesResponse = getGoldPricesResponse(page);
        return GoldPricesPagedResponse.builder()
                .pricesResponse(goldPricesResponse)
                .page(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

    public GoldPricesResponse toPricesResponse(
            @NotEmpty(message = PRICES_CANNOT_BE_NULL_OR_EMPTY)
            List<GoldPrice> prices) {
        return GoldPricesResponse.builder()
                .prices(toResponseListWithServer(prices))
                .build();
    }

    public GoldPricesResponse toPricesRegionResponse(
            @NotEmpty(message = PRICES_CANNOT_BE_NULL_OR_EMPTY)
            List<GoldPrice> prices) {

        String region = prices.get(0).getServer().getRegion().toString();
        List<GoldPriceResponse> pricesResponse = toResponseListWithServer(prices);
        return GoldPricesResponse.builder()
                .region(region)
                .prices(pricesResponse)
                .build();
    }

    public GoldPricesResponse toPricesFactionResponse(
            @NotEmpty(message = PRICES_CANNOT_BE_NULL_OR_EMPTY)
            List<GoldPrice> prices) {
        String faction = prices.get(0).getServer().getFaction().toString();
        return GoldPricesResponse.builder()
                .faction(faction)
                .prices(toResponseListWithServer(prices))
                .build();
    }

    @Named("serverName")
    protected String serverName(
            @NotNull(message = "Server cannot be null")
            Server server) {
        return server.getUniqueName();
    }

    private GoldPricesResponse getGoldPricesResponse(Page<GoldPrice> page) {
        return GoldPricesResponse.builder()
                .prices(toResponseListWithServer(page.getContent()))
                .build();
    }
}