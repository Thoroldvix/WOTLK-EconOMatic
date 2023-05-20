package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.server.Region;
import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.vaadin.flow.router.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final ServerService serverServiceImpl;

    private final PriceMapper priceMapper;

    @Override
    public PriceResponse getPriceForServer(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findMostRecentPriceByServer(server).map(priceMapper::toPriceResponse)
                .orElseThrow(() -> new NotFoundException("No price found for server: " + server.getName()));
    }

    @Override
    public PriceResponse getPriceForServer(Server server) {
        Objects.requireNonNull(server, "Server must not be null");
        return priceRepository.findMostRecentPriceByServer(server).map(priceMapper::toPriceResponse)
                .orElseThrow(() -> new NotFoundException("No price found for server: " + server.getName()));
    }

    @Override
    @Transactional
    public void savePrice(int serverId, PriceResponse recentPrice) {
        Objects.requireNonNull(recentPrice, "Price must not be null");
        Server server = serverServiceImpl.getServerById(serverId);
        Price price = priceMapper.toPrice(recentPrice);
        price.setServer(server);
        priceRepository.save(price);
    }

    @Override
    public BigDecimal getAvgPriceForRegion(Region region) {
        Objects.requireNonNull(region, "Region must not be null");
        LocalDateTime startDate = LocalDateTime.now().minusDays(14);
        return priceRepository.findAvgPriceForRegion(region, startDate).map(BigDecimal::new)
                .orElseThrow(() -> new NotFoundException("No avg price found for region: " + region.name()));
    }


    @Override
    public List<PriceResponse> getAllPricesForServer(String serverName, Pageable pageable) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findAllByServer(server, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public List<PriceResponse> getAllPricesForServer(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findAllByServer(server).stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public List<PriceResponse> getAllPricesForServer(int serverId) {
        if (serverId <= 0) {
            throw new IllegalArgumentException("Server id must be positive");
        }
        Server server = serverServiceImpl.getServerById(serverId);
        return priceRepository.findAllByServer(server).stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public BigDecimal getAvgPriceForServer(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        LocalDateTime startDate = LocalDateTime.now().minusDays(14);
        return priceRepository.findAvgPriceForServer(server, startDate).map(BigDecimal::new)
                .orElseThrow(() -> new NotFoundException("No avg price found for server: " + server.getName()));
    }
}