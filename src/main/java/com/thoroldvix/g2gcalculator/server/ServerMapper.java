package com.thoroldvix.g2gcalculator.server;

import com.thoroldvix.g2gcalculator.price.PriceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { PriceMapper.class})
public interface ServerMapper {


    ServerResponse toServerResponse(Server server);

}