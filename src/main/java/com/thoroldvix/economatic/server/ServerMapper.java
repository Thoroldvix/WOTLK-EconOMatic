package com.thoroldvix.economatic.server;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ServerMapper {

    ServerResponse toResponse(Server server);

    List<ServerResponse> toList(List<Server> servers);

    default ServerListResponse toServerListResponse(List<Server> servers) {
        return new ServerListResponse(toList(servers));
    }

}