package com.thoroldvix.pricepal.item.service;

import com.thoroldvix.pricepal.item.dto.AuctionHouseInfo;
import com.thoroldvix.pricepal.item.dto.FullAuctionHouseInfo;

public interface AuctionHouseService {

    AuctionHouseInfo getAuctionHouseInfo(String serverName);

    FullAuctionHouseInfo getFullAuctionHouseInfo(String serverName);
}
