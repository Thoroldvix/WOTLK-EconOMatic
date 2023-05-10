package com.thoroldvix.g2gcalculator.item.price;

import com.thoroldvix.g2gcalculator.item.dto.AuctionHouseInfo;

import java.util.List;

public interface AuctionHouseService {

    List<AuctionHouseInfo> getAuctionHouseItemsForServer(String server);
}
