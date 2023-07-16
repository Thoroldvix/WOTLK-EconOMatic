package com.thoroldvix.economatic.stats.goldprice;

import com.thoroldvix.economatic.goldprice.GoldPriceResponse;
import com.thoroldvix.economatic.stats.StatsProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface GoldPriceStatMapper {

    int SCALE = 6;
    RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Mapping(target = "median", qualifiedByName = "mapMedian")
    @Mapping(target = "mean", qualifiedByName = "mapMean")
    @Mapping(target = "count", qualifiedByName = "mapCount")
    @Mapping(target = "minimum", source = "min")
    @Mapping(target = "maximum", source = "max")
    GoldPriceStatResponse toResponse(StatsProjection statProj, GoldPriceResponse min, GoldPriceResponse max);


    @Named("mapCount")
    default long mapCount(long count) {
        return count;
    }

    @Named("mapMean")
    default BigDecimal mapMean(Number mean) {
        return BigDecimal.valueOf(mean.doubleValue())
                .setScale(SCALE, ROUNDING_MODE);
    }

    @Named("mapMedian")
    default BigDecimal mapMedian(Number median) {
        return BigDecimal.valueOf(median.doubleValue())
                .setScale(6, ROUNDING_MODE);
    }
}
