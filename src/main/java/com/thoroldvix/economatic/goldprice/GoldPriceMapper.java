package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.server.Server;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Validated
public interface GoldPriceMapper {




    @Mapping(target = "price", source = "value")
    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    GoldPriceResponse toResponseWithServer(GoldPrice goldPrice);

    @Mapping(target = "price", source = "value")
    @Mapping(target = "server", ignore = true)
    GoldPriceResponse toResponse(GoldPrice goldPrice);




    default List<GoldPriceResponse> toResponseListWithServer(List<GoldPrice> prices) {
        return prices.stream().map(this::toResponseWithServer).toList();
    }

    default List<GoldPriceResponse> toResponseList(List<GoldPrice> prices) {
        return prices.stream().map(this::toResponse).toList();
    }

    @Named("serverName")
    default String serverName(@NotNull(message = "Server cannot be null") Server server) {
        return server.getUniqueName();
    }
}