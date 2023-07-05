package com.thoroldvix.economatic.goldprice.mapper;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceStatResponse;
import com.thoroldvix.economatic.shared.StatsProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = GoldPriceMapper.class)
public interface GoldPriceStatMapper {
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
