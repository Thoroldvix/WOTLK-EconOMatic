package com.thoroldvix.g2gcalculator.mapper;

import com.thoroldvix.g2gcalculator.dto.RealmResponse;
import com.thoroldvix.g2gcalculator.model.Realm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {AuctionHouseMapper.class, PriceMapper.class})
public interface RealmMapper {


    RealmResponse toRealmResponse(Realm realm);

}