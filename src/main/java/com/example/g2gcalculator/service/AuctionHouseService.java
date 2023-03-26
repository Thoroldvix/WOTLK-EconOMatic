package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.AuctionHouseResponse;
import com.example.g2gcalculator.dto.ItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;


public interface AuctionHouseService {

    AuctionHouseResponse getAuctionHouseByRealmName(String realmName);

    AuctionHouseResponse getAuctionHouseById(Integer auctionHouseId);

    List<ItemResponse> getAllItemsByAuctionHouseId(Integer auctionHouseId);

    ItemResponse getAuctionHouseItem(Integer auctionHouseId, Integer itemId);
}