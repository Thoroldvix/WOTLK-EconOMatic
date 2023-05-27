package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.*;
import com.thoroldvix.pricepal.server.error.ServerPriceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ServerPriceServiceImpl implements ServerPriceService {

    private final ServerPriceRepository serverPriceRepository;
    private final ServerService serverServiceImpl;
    private final ServerPriceMapper serverPriceMapper;


    private final LocalDateTime AVERAGE_TIME_PERIOD = LocalDateTime.now().minusDays(14);

    @Override
    public ServerPriceResponse getPriceForServer(String serverName) {
        verifyServerName(serverName);
        return serverPriceRepository.findMostRecentPriceByUniqueServerName(serverName).map(serverPriceMapper::toPriceResponse)
                .orElseThrow(() -> new ServerPriceNotFoundException("No price found for server: " + serverName));
    }

    @Override
    public ServerPriceResponse getPriceForServer(int serverId) {
        verifyServerId(serverId);
        return serverPriceRepository.findMostRecentPriceByServerId(serverId).map(serverPriceMapper::toPriceResponse)
                .orElseThrow(() -> new ServerPriceNotFoundException("No price found for server: " + serverId));
    }

    @Override
    public ServerPriceResponse getPriceForServer(Server server) {
        Objects.requireNonNull(server, "Server must not be null");
        return serverPriceRepository.findMostRecentPriceByServer(server).map(serverPriceMapper::toPriceResponse)
                .orElseThrow(() -> new ServerPriceNotFoundException("No price found for server: " + server.getName()));
    }

    @Override
    @Transactional
    public void savePrice(int serverId, ServerPriceResponse recentPrice) {
        Objects.requireNonNull(recentPrice, "Recent server price must not be null");
        verifyServerId(serverId);
        Server server = serverServiceImpl.getServer(serverId);
        ServerPrice serverPrice = serverPriceMapper.toServerPrice(recentPrice);
        serverPrice.setServer(server);
        serverPriceRepository.save(serverPrice);
    }

    @Override
    public ServerPriceResponse getAvgPriceForRegion(Region region) {
        Objects.requireNonNull(region, "Region must not be null");
        BigDecimal price = serverPriceRepository.findAvgPriceByRegion(region, AVERAGE_TIME_PERIOD)
                .orElseThrow(() -> new ServerPriceNotFoundException("No avg price found for region: " + region.name()));

        return ServerPriceResponse.builder()
                .value(price)
                .currency(Currency.USD)
                .build();
    }

    @Override
    public List<ServerPriceResponse> getAllPricesForRegion(Region region, Pageable pageable) {
        Objects.requireNonNull(region, "Region must not be null");
        Objects.requireNonNull(pageable, "Pageable parameter must not be null");
        List<ServerPriceResponse> prices = serverPriceRepository.findAllPricesByRegion(region, pageable).stream()
                .map(serverPriceMapper::toPriceResponse)
                .toList();
        if (prices.isEmpty()) {
            throw new ServerPriceNotFoundException("No prices found for region: " + region.name());
        }
        return prices;
    }

    @Override
    public List<ServerPriceResponse> getAllPricesForFaction(Faction faction, Pageable pageable) {
        Objects.requireNonNull(faction, "Faction must not be null");
        Objects.requireNonNull(pageable, "Pageable parameter must not be null");
        List<ServerPriceResponse> prices = serverPriceRepository.findAllPricesByFaction(faction, pageable).stream()
                .map(serverPriceMapper::toPriceResponse)
                .toList();
        if (prices.isEmpty()) {
            throw new ServerPriceNotFoundException("No prices found for faction: " + faction.name());
        }
        return prices;
    }

    @Override
    public ServerPriceResponse getAvgPriceForFaction(Faction faction) {
        Objects.requireNonNull(faction, "Faction must not be null");
        BigDecimal price = serverPriceRepository.findAvgPriceByFaction(faction, AVERAGE_TIME_PERIOD)
                .orElseThrow(() -> new ServerPriceNotFoundException("No avg price found for faction: " + faction.name()))
                .setScale(5, RoundingMode.HALF_UP);

        return ServerPriceResponse.builder()
                .value(price)
                .currency(Currency.USD)
                .build();
    }

    @Override
    public List<ServerPriceResponse> getAllPricesForServer(String serverName, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable parameter cannot be null");
        verifyServerName(serverName);
        List<ServerPriceResponse> prices = serverPriceRepository.findAllByUniqueServerName(serverName, pageable).getContent().stream()
                .map(serverPriceMapper::toPriceResponse)
                .toList();
        if (prices.isEmpty()) {
            throw new ServerPriceNotFoundException("No prices found for server: " + serverName);
        }
        return prices;
    }

    @Override
    public List<ServerPriceResponse> getAllPricesForServer(int serverId, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable parameter cannot be null");
        verifyServerId(serverId);
        List<ServerPriceResponse> prices = serverPriceRepository.findAllPricesByServerId(serverId, pageable)
                .stream()
                .map(serverPriceMapper::toPriceResponse)
                .toList();
        if (prices.isEmpty()) {
            throw new ServerPriceNotFoundException("No prices found for server ID: " + serverId);
        }
        return prices;
    }


    @Override
    public ServerPriceResponse getAvgPriceForServer(String serverName) {
        verifyServerName(serverName);
        BigDecimal price = serverPriceRepository.findAvgPriceByUniqueServerName(serverName, AVERAGE_TIME_PERIOD)
                .orElseThrow(() -> new ServerPriceNotFoundException("No avg price found for server: " + serverName))
                .setScale(5, RoundingMode.HALF_UP);

        return ServerPriceResponse.builder()
                .value(price)
                .currency(Currency.USD)
                .build();
    }

    @Override
    public ServerPriceResponse getAvgPriceForServer(int serverId) {
        verifyServerId(serverId);
        BigDecimal price = serverPriceRepository.findAvgPriceByServerId(serverId, AVERAGE_TIME_PERIOD)
                .orElseThrow(() -> new ServerPriceNotFoundException("No avg price found for server ID: " + serverId))
                .setScale(5, RoundingMode.HALF_UP);

        return ServerPriceResponse.builder()
                .value(price)
                .currency(Currency.USD)
                .build();
    }


    private void verifyServerId(int serverId) {
        if (serverId <= 0) {
            throw new IllegalArgumentException("Server ID must be positive");
        }
    }

    private void verifyServerName(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
    }

    public LocalDateTime getAVERAGE_TIME_PERIOD() {
        return AVERAGE_TIME_PERIOD;
    }
}