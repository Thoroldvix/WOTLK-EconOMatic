package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.g2g.G2GService;
import com.thoroldvix.g2gcalculator.server.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final ServerService classicServerService;

    private final G2GService g2GService;
    private final PriceMapper priceMapper;
    @Value("${g2g.scraping-interval:PT1H}")
    private Duration priceUpdateInterval;
    @Value("${g2g.force-update:false}")
    private boolean forceUpdate;

    @Transactional
    public PriceResponse getPriceForServerName(String serverName) {
        Server server = classicServerService.getServer(serverName);
        Optional<Price> recentPrice = priceRepository.findMostRecentPriceByServer(server);
        return updatePriceForServer(server, recentPrice);
    }
    @Override
    @Transactional
    public PriceResponse getPriceForServer(Server server) {
        Objects.requireNonNull(server, "Server cannot be null");
        Optional<Price> recentPrice = priceRepository.findMostRecentPriceByServer(server);
        return updatePriceForServer(server, recentPrice);
    }


    private PriceResponse updatePriceForServer(Server server, Optional<Price> recentPrice) {
        Price price;
        if (!requiresUpdate(recentPrice)) {
            price = recentPrice.get();
        } else {
            price = fetchPrice(server);
            priceRepository.save(price);

        }
        return priceMapper.toPriceResponse(price);
    }


    private boolean requiresUpdate(Optional<Price> recentPrice) {
        return recentPrice.isEmpty() || forceUpdate || isPriceStale(recentPrice.get(), priceUpdateInterval);
    }

    @Override
    public List<PriceResponse> getAllPricesForServer(String serverName, Pageable pageable) {
        Server server = classicServerService.getServer(serverName);
        return priceRepository.findAllByServer(server, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    private Price fetchPrice(Server server) {
        Price price = g2GService.fetchServerPrice(server);
        price.setServer(server);
        return price;
    }

    private boolean isPriceStale(Price recentPrice, Duration priceUpdateInterval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime updatedAt = recentPrice.getUpdatedAt();
        return now.isAfter(updatedAt.plus(priceUpdateInterval));
    }

}