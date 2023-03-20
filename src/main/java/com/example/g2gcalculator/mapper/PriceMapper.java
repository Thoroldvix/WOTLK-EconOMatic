package com.example.g2gcalculator.mapper;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Price;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceMapper {

    PriceResponse toPriceResponse(Price price);
}