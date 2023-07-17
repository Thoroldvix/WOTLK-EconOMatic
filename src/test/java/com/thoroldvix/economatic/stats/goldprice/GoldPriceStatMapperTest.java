package com.thoroldvix.economatic.stats.goldprice;

import com.thoroldvix.economatic.goldprice.GoldPriceResponse;
import com.thoroldvix.economatic.stats.StatsProjection;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

class GoldPriceStatMapperTest {

    private static final BigDecimal MEAN = BigDecimal.valueOf(0.1).setScale(6, RoundingMode.HALF_UP);
    private static final int MAX_ID = 1;
    private static final int MIN_ID = 2;
    private static final BigDecimal MEDIAN = BigDecimal.valueOf(0.2).setScale(6, RoundingMode.HALF_UP);
    private static final int COUNT = 3;
    private final GoldPriceStatMapper priceStatMapper = Mappers.getMapper(GoldPriceStatMapper.class);

    @Test
    void toResponse_returnsValidGoldPriceStatResponse_whenValidStatProjectionProvided() {
        StatsProjection statsProjection = getStatProjection();
        GoldPriceResponse min = GoldPriceResponse.builder().build();
        GoldPriceResponse max = GoldPriceResponse.builder().build();
        GoldPriceStatResponse expected = buildGoldPriceStatResponse(max, min);

        GoldPriceStatResponse actual = priceStatMapper.toResponse(statsProjection, min, max);

        assertThat(actual).isEqualTo(expected);
    }

    private static GoldPriceStatResponse buildGoldPriceStatResponse(GoldPriceResponse max, GoldPriceResponse min) {
        return GoldPriceStatResponse.builder()
                .count(COUNT)
                .maximum(max)
                .mean(MEAN)
                .median(MEDIAN)
                .minimum(min)
                .build();
    }

    private StatsProjection getStatProjection() {
        return new StatsProjection() {
            @Override
            public BigDecimal getMean() {
                return GoldPriceStatMapperTest.MEAN;
            }

            @Override
            public Number getMaxId() {
                return GoldPriceStatMapperTest.MAX_ID;
            }

            @Override
            public Number getMinId() {
                return GoldPriceStatMapperTest.MIN_ID;
            }

            @Override
            public BigDecimal getMedian() {
                return GoldPriceStatMapperTest.MEDIAN;
            }

            @Override
            public long getCount() {
                return GoldPriceStatMapperTest.COUNT;
            }
        };
    }

}