package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.server.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceMapper {
    @Mapping(target = "serverName", source = "server", qualifiedByName = "serverName")
    PriceResponse toPriceResponse(Price price);

    @Named("serverName")
    default String serverName(Server server) {
        return String.format("%s [%s] - %s", server.getName(),
                server.getRegion(), server.getFaction().toString());
    }
}