package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.shared.StatsProjection;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = GoldPriceMapper.class)
@Validated
public abstract class GoldPriceStatMapper {

    private static final GoldPriceMapper GOLD_PRICE_MAPPER = Mappers.getMapper(GoldPriceMapper.class);


    public GoldPriceStatResponse toResponse(
            @NotNull(message = "Stats projection cannot be null")
            StatsProjection statProj,
            @NotNull(message = "Stat repository cannot be null")
            GoldPriceStatRepository statRepository) {
        GoldPriceResponse min = getMin(statProj, statRepository);
        GoldPriceResponse max = getMax(statProj, statRepository);
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

    private GoldPriceResponse getMax(StatsProjection goldPriceStat, GoldPriceStatRepository goldPriceStatRepository) {
        long maxId = goldPriceStat.getMaxId().longValue();
        return goldPriceStatRepository.findById(maxId).map(GOLD_PRICE_MAPPER::toResponse)
                .orElseThrow(() -> new GoldPriceNotFoundException("No max gold price found with id " + maxId));
    }

    private GoldPriceResponse getMin(StatsProjection goldPriceStat, GoldPriceStatRepository goldPriceStatRepository) {
        long minId = goldPriceStat.getMinId().longValue();
        return goldPriceStatRepository.findById(minId).map(GOLD_PRICE_MAPPER::toResponse)
                .orElseThrow(() -> new GoldPriceNotFoundException("No min gold price found with id " + minId));
    }
}
