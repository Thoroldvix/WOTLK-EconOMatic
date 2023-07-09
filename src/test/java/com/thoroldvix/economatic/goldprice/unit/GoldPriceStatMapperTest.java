package com.thoroldvix.economatic.goldprice.unit;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceStatResponse;
import com.thoroldvix.economatic.goldprice.GoldPriceStatMapper;
import com.thoroldvix.economatic.shared.StatsProjection;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GoldPriceStatMapperTest {

    private static final String SERVER_NAME = "everlook-alliance";
    private static final BigDecimal MIN_PRICE = BigDecimal.valueOf(0.1);
    private static final BigDecimal MAX_PRICE = BigDecimal.valueOf(0.2);
    private static final BigDecimal MEAN = BigDecimal.valueOf(0.001122);
    private static final int MAX_ID = 41003;
    private static final int MIN_ID = 41003;
    private static final BigDecimal MEDIAN = BigDecimal.valueOf(0.001136);
    private static final int COUNT = 9;
    private final GoldPriceStatMapper priceStatMapper = Mappers.getMapper(GoldPriceStatMapper.class);

    @Test
    void toResponse_returnsValidGoldPriceStatResponse_whenValidStatProjectionProvided() {
        StatsProjection statsProjection = getStatProjection();
        LocalDateTime now = LocalDateTime.now();
        GoldPriceResponse min = new GoldPriceResponse(MIN_PRICE, SERVER_NAME, now);
        GoldPriceResponse max = new GoldPriceResponse(MAX_PRICE, SERVER_NAME, now);
        GoldPriceStatResponse expected = getGoldPriceStatResponse(max, min);
        GoldPriceStatResponse actual = priceStatMapper.toResponse(statsProjection, min, max);

        assertThat(actual).isEqualTo(expected);
    }

    private GoldPriceStatResponse getGoldPriceStatResponse(GoldPriceResponse max, GoldPriceResponse min) {
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
                return MEAN;
            }

            @Override
            public Number getMaxId() {
                return MAX_ID;
            }

            @Override
            public Number getMinId() {
                return MIN_ID;
            }

            @Override
            public BigDecimal getMedian() {
                return MEDIAN;
            }

            @Override
            public long getCount() {
                return COUNT;
            }
        };
    }
}