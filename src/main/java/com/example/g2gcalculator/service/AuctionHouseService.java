package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.AuctionHouseResponse;
import com.example.g2gcalculator.dto.ItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;


public interface AuctionHouseService {

    List<AuctionHouseResponse> getAuctionHousesByRealmId(Integer realmId);

    List<ItemResponse> getAllItemsByAuctionHouseId(Integer auctionHouseId);
}