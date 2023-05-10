package com.thoroldvix.g2gcalculator.item.price;

import com.thoroldvix.g2gcalculator.item.dto.RealMoneyItemPrice;

public interface RMItemPriceService {


    RealMoneyItemPrice getPriceForItem(String serverName, String itemName, int amount, boolean minBuyout);

}