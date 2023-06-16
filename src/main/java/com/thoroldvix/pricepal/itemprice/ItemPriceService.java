package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.item.ItemService;
import com.thoroldvix.pricepal.item.ItemServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemPriceService {


    private final NexusHubClient nexusHubClient;
    private final ItemService itemServiceImpl;
    private final ItemPriceRepository itemPriceRepository;
    private final JdbcTemplate jdbcTemplate;

    public AuctionHouseInfo getAuctionHouseInfo(String serverName) {
        verifyServerName(serverName);
        return nexusHubClient.getAllItemPricesForServer(serverName);
    }

    private void verifyServerName(String serverName) {
        Objects.requireNonNull(serverName, "Server name must not be null");
        Pattern serverNamePattern = Pattern.compile("^([^-']+)-(.+)$");
        Matcher matcher = serverNamePattern.matcher(serverName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Server name must be valid");
        }
    }

    @Transactional
    public void saveAll(List<ItemPrice> itemPricesToSave) {
        Objects.requireNonNull(itemPricesToSave, "Item prices cannot be null");
        itemPriceRepository.saveAll(itemPricesToSave, jdbcTemplate);
    }
}
