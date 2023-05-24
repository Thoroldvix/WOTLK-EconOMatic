package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ServerPriceMapper.class})
public interface ServerMapper {


    @Mapping(target = "price", source = "serverPrices", qualifiedByName = "mostRecentPrice")
    ServerResponse toServerResponse(Server server);

    @Named("mostRecentPrice")
    default ServerPrice mostRecentPrice(List<ServerPrice> serverPrices) {
        return serverPrices.stream()
                .max(Comparator.comparing(ServerPrice::getLastUpdated))
                .orElse(null);
    }
}