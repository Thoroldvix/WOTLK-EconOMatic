package com.thoroldvix.pricepal.population;

import com.thoroldvix.pricepal.server.Server;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PopulationMapper {

    @Mapping(target = "serverName", source = "server", qualifiedByName = "name")
    PopulationResponse toResponse(Population population);

    List<PopulationResponse> toResponseList(List<Population>
                                                              population);

    @Named("name")
    default String serverName(Server server) {
        return server.getUniqueName();
    }
}
