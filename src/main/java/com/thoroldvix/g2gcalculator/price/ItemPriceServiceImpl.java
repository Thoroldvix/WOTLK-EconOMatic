package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.item.ItemService;
import com.thoroldvix.g2gcalculator.item.ItemStats;
import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.server.ServerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
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
    public ItemPriceResponse getPriceForItem(String serverName, String itemIdentifier, int amount, boolean minBuyout) {
        if (amount < 1 || !StringUtils.hasText(itemIdentifier)) {
            throw new IllegalArgumentException("Amount and item identifier must be valid");
        }

        Server server = serverServiceImpl.getServer(serverName);
        ItemStats item;

        if (NumberUtils.isCreatable(itemIdentifier)) {
            item = itemServiceImpl.getItemById(serverName, Integer.parseInt(itemIdentifier));
        } else {
            item = itemServiceImpl.getItemByName(serverName, itemIdentifier);
        }

        PriceResponse updatedPrice = priceServiceImpl.getPriceForServer(server);
        long targetPrice = minBuyout ? item.minBuyout() : item.marketValue();
        BigDecimal itemPrice = itemPriceCalculatorImpl.calculatePrice(targetPrice, updatedPrice, amount);

        return ItemPriceResponse.builder()
                .currency(item.currency())
                .price(itemPrice)
                .build();
    }
}