package com.thoroldvix.economatic.goldprice.mapper;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceStatResponse;
import com.thoroldvix.economatic.shared.StatsProjection;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = GoldPriceMapper.class)
@Validated
public abstract class GoldPriceStatMapper {

    public GoldPriceStatResponse toResponse(
            @NotNull(message = "Stats projection cannot be null")
            StatsProjection statProj,
            @NotNull(message = "Min price cannot be null")
            GoldPriceResponse min,
            @NotNull(message = "Max price cannot ben null")
            GoldPriceResponse max) {

        BigDecimal median = getMedian(statProj);
        BigDecimal mean = getMean(statProj);
        long count = statProj.getCount();

        return GoldPriceStatResponse.builder()
                .minimum(min)
                .median(median)
                .mean(mean)
                .maximum(max)
                .count(count)
                .build();
    }

    private static BigDecimal getMean(StatsProjection statProj) {
        return BigDecimal.valueOf(statProj.getMean().doubleValue())
                .setScale(6, RoundingMode.HALF_UP);
    }

    private static BigDecimal getMedian(StatsProjection statProj) {
        return BigDecimal.valueOf(statProj.getMedian().doubleValue())
                .setScale(6, RoundingMode.HALF_UP);
    }


}
