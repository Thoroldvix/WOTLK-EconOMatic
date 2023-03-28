package com.thoroldvix.g2gcalculator.mapper;

import com.thoroldvix.g2gcalculator.dto.PriceResponse;
import com.thoroldvix.g2gcalculator.model.Price;
import com.thoroldvix.g2gcalculator.model.Realm;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceMapper {

    @Mapping(target = "realmName", source = "realm", qualifiedByName = "mapRealmName")
    PriceResponse toPriceResponse(Price price);

    @Named(value = "mapPriceValue")
   default String mapPriceValue(BigDecimal bigDecimal) {
        return bigDecimal.multiply(BigDecimal.valueOf(1000)) + "/1k";
    }
    @Named(value = "mapRealmName")
    default String mapRealmName(Realm realm) {
        return realm.getName() + "-" + realm.getFaction();
    }
}