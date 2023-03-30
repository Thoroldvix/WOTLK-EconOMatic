package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.items.ItemService;
import com.thoroldvix.g2gcalculator.items.ItemStats;
import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.server.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemPriceServiceImpl implements ItemPriceService {
    private final PriceService priceServiceImpl;

    private final ServerService serverServiceImpl;
    private final ItemService itemServiceImpl;

    private final ItemPriceCalculator itemPriceCalculatorImpl;


    @Override
    @Transactional
    public ItemPriceResponse getPriceForItem(String serverName, int itemId, int amount, boolean minBuyout) {
        if (amount < 1 || itemId < 1) {
            throw new IllegalArgumentException("Server name, amount, and item id must be valid");
        }
        Server server = serverServiceImpl.getServer(serverName);
        ItemStats item = itemServiceImpl.getItemById(serverName, itemId);
        PriceResponse updatedPrice = priceServiceImpl.getPriceForServer(server);

        long targetPrice = minBuyout ? item.minBuyout() : item.marketValue();
        BigDecimal itemPrice = itemPriceCalculatorImpl.calculatePrice(targetPrice, updatedPrice, amount);

        return ItemPriceResponse.builder()
                .currency(item.currency())
                .price(itemPrice)
                .build();
    }

    @Override
    @Transactional
    public ItemPriceResponse getPriceForItem(String serverName, String itemName, int amount, boolean minBuyout) {
        if (amount < 1 || !StringUtils.hasText(itemName)) {
            throw new IllegalArgumentException("Amount, and item name must be valid");
        }
        Server server = serverServiceImpl.getServer(serverName);
        ItemStats item = itemServiceImpl.getItemByName(serverName, itemName);
        PriceResponse updatedPrice = priceServiceImpl.getPriceForServer(server);

        long priceType = minBuyout ? item.minBuyout() : item.marketValue();
        BigDecimal itemPrice = itemPriceCalculatorImpl.calculatePrice(priceType, updatedPrice, amount);

        return ItemPriceResponse.builder()
                .currency(updatedPrice.currency())
                .price(itemPrice)
                .build();
    }
}