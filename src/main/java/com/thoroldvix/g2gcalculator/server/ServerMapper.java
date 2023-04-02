package com.thoroldvix.g2gcalculator.server;

import com.thoroldvix.g2gcalculator.price.Price;
import com.thoroldvix.g2gcalculator.price.PriceMapper;
import com.thoroldvix.g2gcalculator.price.PriceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { PriceMapper.class})
public interface ServerMapper {


    @Mapping(target = "price", source = "prices", qualifiedByName = "mostRecentPrice")
    ServerResponse toServerResponse(Server server);

    @Named("mostRecentPrice")
    default Price mostRecentPrice(List<Price> prices) {
        return prices.stream()
                .max(Comparator.comparing(Price::getUpdatedAt))
                .orElse(null);
    }
}