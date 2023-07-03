package com.thoroldvix.economatic.server.mapper;

import com.thoroldvix.economatic.server.dto.ServerListResponse;
import com.thoroldvix.economatic.server.dto.ServerResponse;
import com.thoroldvix.economatic.server.model.Server;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServerMapper {

    ServerResponse toResponse(Server server);

    List<ServerResponse> toList(List<Server> servers);

    default ServerListResponse toServerListResponse(List<Server> servers) {
        return new ServerListResponse(toList(servers));
    }

}