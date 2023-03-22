package com.example.g2gcalculator.mapper;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Price;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceMapper {

    @Mapping(target = "price", source ="value" ,qualifiedByName = "map")
    PriceResponse toPriceResponse(Price price);


//    Price fromPriceResponse(PriceResponse priceResponse);

    @Named(value = "map")
   default String map(BigDecimal bigDecimal) {
        return bigDecimal.multiply(BigDecimal.valueOf(1000)) + "/1k";
    }
}