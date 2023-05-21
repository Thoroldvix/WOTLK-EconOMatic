package com.thoroldvix.g2gcalculator.server.service;

import com.thoroldvix.g2gcalculator.server.dto.ServerPrice;
import com.thoroldvix.g2gcalculator.server.entity.Price;
import com.thoroldvix.g2gcalculator.server.entity.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceMapper {
    @Mapping(target = "serverName", source = "server", qualifiedByName = "serverName")
    ServerPrice toPriceResponse(Price price);


    @Mapping(target = "server", ignore = true)
    @Mapping(target = "id", ignore = true)
    Price toPrice(ServerPrice serverPrice);

    @Named("serverName")
    default String serverName(Server server) {
        return String.format("%s [%s] - %s", server.getName(),
                server.getRegion(), server.getFaction().toString());
    }
}