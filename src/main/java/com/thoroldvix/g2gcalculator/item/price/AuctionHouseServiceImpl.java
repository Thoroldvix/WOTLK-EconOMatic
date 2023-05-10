package com.thoroldvix.g2gcalculator.item.price;

import com.thoroldvix.g2gcalculator.common.StringFormatter;
import com.thoroldvix.g2gcalculator.item.ItemsClient;
import com.thoroldvix.g2gcalculator.item.dto.AuctionHouseInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionHouseServiceImpl implements AuctionHouseService {

    private final ItemsClient itemsClient;
    @Override
    public List<AuctionHouseInfo> getAuctionHouseItemsForServer(String server) {
         if (!StringUtils.hasText(server)) {
            throw new IllegalArgumentException("Server name must be valid");
        }
        String formattedServerName = formatServerName(server);
        return itemsClient.getAllItemPricesForServer(formattedServerName).items();

    }



    private String formatServerName(String serverName) {
         return StringFormatter.formatString(serverName, word -> word.replaceAll("'", ""));
    }
}
