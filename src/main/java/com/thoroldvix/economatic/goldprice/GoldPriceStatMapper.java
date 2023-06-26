package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.shared.StatsProjection;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = GoldPriceMapper.class)
public interface GoldPriceStatMapper {

    GoldPriceMapper GOLD_PRICE_MAPPER = Mappers.getMapper(GoldPriceMapper.class);


    default GoldPriceStatResponse toResponse(StatsProjection statProj,
                                             GoldPriceStatRepository statRepository) {
        Objects.requireNonNull(statProj, "StatsProjection cannot be null");
        Objects.requireNonNull(statRepository, "Stat repository cannot be null");
        GoldPriceResponse min = getMin(statProj, statRepository);
        GoldPriceResponse max = getMax(statProj, statRepository);
        BigDecimal median = BigDecimal.valueOf(statProj.getMedian().doubleValue())
                .setScale(6, RoundingMode.HALF_UP);
        BigDecimal mean = BigDecimal.valueOf(statProj.getMean().doubleValue())
                .setScale(6, RoundingMode.HALF_UP);
        long count = statProj.getCount();

        return GoldPriceStatResponse.builder()
                .minimum(min)
                .median(median)
                .mean(mean)
                .maximum(max)
                .count(count)
                .build();
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
