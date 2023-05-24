package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.PriceRepository;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
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
public class ServerPriceServiceImpl implements ServerPriceService {

    private final PriceRepository priceRepository;
    private final ServerService serverServiceImpl;

    private final ServerPriceMapper serverPriceMapper;

    @Override
    public ServerPriceResponse getPriceForServer(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findMostRecentPriceByServer(server).map(serverPriceMapper::toPriceResponse)
                .orElseThrow(() -> new NotFoundException("No price found for server: " + server.getName()));
    }

    @Override
    public ServerPriceResponse getPriceForServer(Server server) {
        Objects.requireNonNull(server, "Server must not be null");
        return priceRepository.findMostRecentPriceByServer(server).map(serverPriceMapper::toPriceResponse)
                .orElseThrow(() -> new NotFoundException("No price found for server: " + server.getName()));
    }

    @Override
    @Transactional
    public void savePrice(int serverId, ServerPriceResponse recentPrice) {
        Objects.requireNonNull(recentPrice, "Price must not be null");
        Server server = serverServiceImpl.getServer(serverId);
        ServerPrice serverPrice = serverPriceMapper.toPrice(recentPrice);
        serverPrice.setServer(server);
        priceRepository.save(serverPrice);
    }

    @Override
    public ServerPriceResponse getAvgPriceForRegion(Region region) {
        Objects.requireNonNull(region, "Region must not be null");
        LocalDateTime startDate = LocalDateTime.now().minusDays(14);
        BigDecimal price = priceRepository.findAvgPriceForRegion(region, startDate).map(BigDecimal::new)
                .orElseThrow(() -> new NotFoundException("No avg price found for region: " + region.name()));
        return ServerPriceResponse.builder()
                .value(price)
                .lastUpdated(LocalDateTime.now())
                .build();
    }


    @Override
    public List<ServerPriceResponse> getAllPricesForServer(String serverName, Pageable pageable) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findAllByServer(server, pageable).getContent().stream()
                .map(serverPriceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public List<ServerPriceResponse> getAllPricesForServer(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findAllByServer(server).stream()
                .map(serverPriceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public List<ServerPriceResponse> getAllPricesForServer(int serverId) {
        if (serverId <= 0) {
            throw new IllegalArgumentException("Server id must be positive");
        }
        Server server = serverServiceImpl.getServer(serverId);
        return priceRepository.findAllByServer(server).stream()
                .map(serverPriceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public ServerPriceResponse getAvgPriceForServer(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        LocalDateTime startDate = LocalDateTime.now().minusDays(14);
        BigDecimal price = priceRepository.findAvgPriceForServer(server, startDate).map(BigDecimal::new)
                .orElseThrow(() -> new NotFoundException("No avg price found for server: " + server.getName()));

        return ServerPriceResponse.builder()
                .value(price)
                .serverName(server.getName())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}