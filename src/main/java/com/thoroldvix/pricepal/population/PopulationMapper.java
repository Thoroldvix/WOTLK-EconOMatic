package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.Server;
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
