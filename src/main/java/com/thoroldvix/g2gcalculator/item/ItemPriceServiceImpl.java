package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.price.PriceService;
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
        ItemStats itemStats = getItemStats(serverName, itemIdentifier);


        PriceResponse serverPrice = priceServiceImpl.getPriceForServer(server);
        long targetPrice = getTargetPrice(minBuyout, itemStats);
        BigDecimal itemPrice = itemPriceCalculatorImpl.calculatePrice(targetPrice, serverPrice, amount);

        return ItemPriceResponse.builder()
                .currency(serverPrice.currency())
                .value(itemPrice)
                .build();
    }

    private ItemStats getItemStats(final String serverName, final String itemIdentifier) {
    if (NumberUtils.isCreatable(itemIdentifier)) {
        return itemServiceImpl.getItemById(serverName, Integer.parseInt(itemIdentifier));
    } else {
        return itemServiceImpl.getItemByName(serverName, itemIdentifier);
    }
}

    private long getTargetPrice(boolean minBuyout, ItemStats item) {
        return minBuyout ? item.minBuyout() : item.marketValue();
    }
}