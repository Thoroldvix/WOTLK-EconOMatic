package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.server.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GoldPriceMapper {


    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    GoldPriceResponse toResponseWithServerName(GoldPrice goldPrice);

    @Mapping(target = "server", ignore = true)
    GoldPriceResponse toResponse(GoldPrice goldPrice);

    default List<GoldPriceResponse> toResponseListWithServerNames(List<GoldPrice> goldPrices) {
        return goldPrices.stream().map(this::toResponseWithServerName).toList();
    }

     default List<GoldPriceResponse> toResponseList(List<GoldPrice> goldPrices) {
        return goldPrices.stream().map(this::toResponse).toList();
    }

    @Named("serverName")
    default String serverName(Server server) {
        return server.getUniqueName();
    }
}