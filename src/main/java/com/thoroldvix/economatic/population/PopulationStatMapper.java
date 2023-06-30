package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.shared.StatsProjection;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = PopulationMapper.class)
public interface PopulationStatMapper {

    PopulationMapper POPULATION_MAPPER = Mappers.getMapper(PopulationMapper.class);

    default PopulationStatResponse toResponse(
            @NotNull(message = "Stat projection cannot be null")
            StatsProjection statProj,
            @NotNull(message = "Min population cannot be null")
            PopulationResponse min,
            @NotNull(message = "Min population cannot be null")
            PopulationResponse max) {

        int median = statProj.getMedian().intValue();
        int mean = statProj.getMean().intValue();

        long count = statProj.getCount();

        return PopulationStatResponse.builder()
                .minimum(min)
                .median(median)
                .mean(mean)
                .maximum(max)
                .count(count)
                .build();
    }




}
