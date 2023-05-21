package com.thoroldvix.g2gcalculator.item.service;

import com.thoroldvix.g2gcalculator.item.api.ItemPriceClient;
import com.thoroldvix.g2gcalculator.item.dto.ItemPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemPriceServiceImpl implements ItemPriceService {
    private final Pattern SERVER_NAME_PATTERN = Pattern.compile("^([^-']+)-(.+)$");

    private final ItemPriceClient itemPriceClient;

    @Override
    public List<ItemPrice> getAllItemPrices(String serverName) {
        if (!StringUtils.hasText(serverName) || !verifyServerName(serverName)) {
            throw new IllegalArgumentException("Server name must be valid");
        }
        return itemPriceClient.getAllItemPricesForServer(serverName).items();
    }




    private boolean verifyServerName(String serverName) {
        Matcher matcher = SERVER_NAME_PATTERN.matcher(serverName);
        return matcher.matches();
    }
}
