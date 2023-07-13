package com.thoroldvix.economatic.stats.population;


import com.thoroldvix.economatic.population.PopulationResponse;
import com.thoroldvix.economatic.stats.StatsProjection;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface PopulationStatMapper {

    default PopulationStatResponse toResponse(StatsProjection statProj, PopulationResponse min, PopulationResponse max) {
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
