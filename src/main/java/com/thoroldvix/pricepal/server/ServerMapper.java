package com.thoroldvix.pricepal.server;

import com.thoroldvix.pricepal.population.PopulationMapper;
import com.thoroldvix.pricepal.goldprice.GoldPriceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {GoldPriceMapper.class, PopulationMapper.class})
public interface ServerMapper {

    ServerResponse toResponse(Server server);

    List<ServerResponse> toResponseList(List<Server> servers);

}