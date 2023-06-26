package com.thoroldvix.economatic.population;

import com.thoroldvix.economatic.shared.StatsProjection;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.Objects;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = PopulationMapper.class)
public interface PopulationStatMapper {

    PopulationMapper POPULATION_MAPPER = Mappers.getMapper(PopulationMapper.class);

    default PopulationStatResponse toResponse(StatsProjection statProj, PopulationStatRepository statRepository) {
        Objects.requireNonNull(statProj, "StatsProjection cannot be null");
        Objects.requireNonNull(statRepository, "Stat repository cannot be null");

        PopulationResponse min = getMin(statProj, statRepository);
        PopulationResponse max = getMax(statProj, statRepository);
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

    default PopulationResponse getMax(StatsProjection statProj, PopulationStatRepository statRepository) {
        long maxId = statProj.getMaxId().longValue();
        return statRepository.findById(maxId).map(POPULATION_MAPPER::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No max population found for id " + maxId));
    }

    default PopulationResponse getMin(StatsProjection statProj, PopulationStatRepository statRepository) {
        long minId = statProj.getMinId().longValue();
        return statRepository.findById(minId).map(POPULATION_MAPPER::toResponse)
                .orElseThrow(() -> new PopulationNotFoundException("No min population found for id " + minId));
    }


}
