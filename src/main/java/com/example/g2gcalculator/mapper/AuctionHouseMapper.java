package com.example.g2gcalculator.mapper;

import com.example.g2gcalculator.dto.AuctionHouseResponse;
import com.example.g2gcalculator.model.AuctionHouse;
import com.example.g2gcalculator.model.Type;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuctionHouseMapper {


    AuctionHouseResponse toAuctionHouseResponse(AuctionHouse auctionHouse);

    List<AuctionHouseResponse> toAuctionHouseResponseList(List<AuctionHouse> auctionHouse);

    @Named("mapToType")
    default String mapToType(Type type) {
        return type.toString();
    }
}