package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.PopulationResponse;
import com.thoroldvix.pricepal.server.entity.Population;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PopulationMapper {

    PopulationResponse toPopulationResponse(Population population);
}
