package com.thoroldvix.g2gcalculator.mapper;

import com.thoroldvix.g2gcalculator.dto.AuctionHouseResponse;
import com.thoroldvix.g2gcalculator.model.AuctionHouse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuctionHouseMapper {


    AuctionHouseResponse toAuctionHouseResponse(AuctionHouse auctionHouse);

    List<AuctionHouseResponse> toAuctionHouseResponseList(List<AuctionHouse> auctionHouse);


}