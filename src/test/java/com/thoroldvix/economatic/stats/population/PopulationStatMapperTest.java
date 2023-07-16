package com.thoroldvix.economatic.stats.population;

import com.thoroldvix.economatic.population.PopulationResponse;
import com.thoroldvix.economatic.stats.StatsProjection;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class PopulationStatMapperTest {

    public static final int MEAN = 10;
    public static final int MAX_ID = 2;
    public static final int MIN_ID = 1;
    public static final int MEDIAN = 5;
    public static final long COUNT = 3;
    private final PopulationStatMapper underTest = Mappers.getMapper(PopulationStatMapper.class);

    @Test
    void toResponse_returnsCorrectPopulationStatResponse() {
        StatsProjection statsProjection = getStatsProjection();
        PopulationResponse min = PopulationResponse.builder().build();
        PopulationResponse max = PopulationResponse.builder().build();
        PopulationStatResponse expected = buildPopulationStatResponse(max, min);

        PopulationStatResponse actual = underTest.toResponse(statsProjection, min, max);

        assertThat(actual).isEqualTo(expected);
    }

    private static PopulationStatResponse buildPopulationStatResponse(PopulationResponse max, PopulationResponse min) {
        return PopulationStatResponse.builder()
                .mean(MEAN)
                .maximum(max)
                .minimum(min)
                .median(MEDIAN)
                .count(COUNT)
                .build();
    }

    private StatsProjection getStatsProjection() {
        return new StatsProjection() {
            @Override
            public Number getMean() {
                return PopulationStatMapperTest.MEAN;
            }

            @Override
            public Number getMaxId() {
                return PopulationStatMapperTest.MAX_ID;
            }

            @Override
            public Number getMinId() {
                return PopulationStatMapperTest.MIN_ID;
            }

            @Override
            public Number getMedian() {
                return PopulationStatMapperTest.MEDIAN;
            }

            @Override
            public long getCount() {
                return PopulationStatMapperTest.COUNT;
            }
        };
    }
}