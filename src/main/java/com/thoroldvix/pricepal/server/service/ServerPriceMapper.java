package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServerPriceMapper {
    @Mapping(target = "serverName", source = "server", qualifiedByName = "serverName")
    ServerPriceResponse toPriceResponse(ServerPrice serverPrice);


    @Mapping(target = "server", ignore = true)
    @Mapping(target = "id", ignore = true)
    ServerPrice toServerPrice(ServerPriceResponse serverPriceResponse);

    @Named("serverName")
    default String serverName(Server server) {
        return server.getUniqueName();
    }
}