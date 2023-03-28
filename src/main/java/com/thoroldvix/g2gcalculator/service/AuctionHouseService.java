package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.AuctionHouseResponse;
import com.thoroldvix.g2gcalculator.dto.ItemResponse;

import java.util.List;


public interface AuctionHouseService {

    AuctionHouseResponse getAuctionHouseByRealmName(String realmName);

    AuctionHouseResponse getAuctionHouseById(Integer auctionHouseId);

    List<ItemResponse> getAllItemsByAuctionHouseId(Integer auctionHouseId);

    ItemResponse getAuctionHouseItem(Integer auctionHouseId, Integer itemId);
}