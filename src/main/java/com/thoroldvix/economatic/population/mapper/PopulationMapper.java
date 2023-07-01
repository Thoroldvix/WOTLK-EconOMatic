package com.thoroldvix.economatic.population.mapper;

import com.thoroldvix.economatic.population.model.Population;
import com.thoroldvix.economatic.population.dto.PopulationResponse;
import com.thoroldvix.economatic.server.model.Server;
import jakarta.validation.constraints.NotEmpty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PopulationMapper {

    String POPULATIONS_CANNOT_BE_NULL_OR_EMPTY = "Populations cannot be null or empty";

    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    PopulationResponse toResponseWithServer(Population population);

    @Mapping(target = "server", ignore = true)
    PopulationResponse toResponse(Population population);

    default List<PopulationResponse> toResponseList(
            @NotEmpty(message = POPULATIONS_CANNOT_BE_NULL_OR_EMPTY)
            List<Population> populations) {
        return populations.stream().map(this::toResponse).toList();
    }

    default List<PopulationResponse> toResponseListWithServer(
            @NotEmpty(message = POPULATIONS_CANNOT_BE_NULL_OR_EMPTY)
            List<Population> populations) {
        return populations.stream().map(this::toResponseWithServer).toList();
    }



    @Named("serverName")
    default String serverName(Server server) {
        Objects.requireNonNull(server, "Server cannot be null");
        return server.getUniqueName();
    }
}
