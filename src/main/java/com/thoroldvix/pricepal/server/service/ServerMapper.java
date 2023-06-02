package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Server;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ServerPriceMapper.class, PopulationMapper.class})
public interface ServerMapper {

    ServerResponse toServerResponse(Server server);

    List<ServerResponse> toServerResponseList(List<Server> servers);

}