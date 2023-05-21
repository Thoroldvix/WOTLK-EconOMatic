package com.thoroldvix.g2gcalculator.item.service;

import com.thoroldvix.g2gcalculator.item.dto.ItemPrice;

import java.util.List;

public interface ItemPriceService {



    List<ItemPrice> getAllItemPrices(String serverName);
}
