package com.thoroldvix.pricepal.goldprice;

import com.thoroldvix.pricepal.server.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GoldPriceMapper {
    @Mapping(target = "serverName", source = "server", qualifiedByName = "serverName")
    GoldPriceResponse toPriceResponse(GoldPrice goldPrice);

    @Mapping(target = "server", ignore = true)
    @Mapping(target = "id", ignore = true)
    GoldPrice toServerPrice(GoldPriceResponse goldPriceResponse);

    List<GoldPriceResponse> toPriceResponseList(List<GoldPrice> goldPrices);

    @Named("serverName")
    default String serverName(Server server) {
        return server.getUniqueName();
    }
}