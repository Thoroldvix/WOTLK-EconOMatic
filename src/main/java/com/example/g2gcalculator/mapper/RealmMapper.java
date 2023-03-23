package com.example.g2gcalculator.mapper;

import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.model.Realm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {AuctionHouseMapper.class, PriceMapper.class})
public interface RealmMapper {


    RealmResponse toRealmResponse(Realm realm);

}