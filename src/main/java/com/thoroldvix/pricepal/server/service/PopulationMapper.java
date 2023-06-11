package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.PopulationResponse;
import com.thoroldvix.pricepal.server.entity.Population;
import com.thoroldvix.pricepal.server.entity.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PopulationMapper {

    @Mapping(target = "serverName", source = "server", qualifiedByName = "serverName")
    PopulationResponse toPopulationResponse(Population population);

    List<PopulationResponse> toPopulationResponseList(List<Population>
                                                              population);

    @Named("serverName")
    default String serverName(Server server) {
        return server.getUniqueName();
    }
}
