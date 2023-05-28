package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.common.ValidationUtils;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.*;
import com.thoroldvix.pricepal.server.error.ServerPriceNotFoundException;
import com.thoroldvix.pricepal.server.repository.ServerPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ServerPriceService  {

    private final ServerPriceRepository serverPriceRepository;
    private final ServerService serverServiceImpl;
    private final ServerPriceMapper serverPriceMapper;


    private final LocalDateTime AVERAGE_TIME_PERIOD = LocalDateTime.now().minusDays(14);


    public ServerPriceResponse getPriceForServer(String serverName) {
        ValidationUtils.validateNonEmptyString(serverName, "Server name must not be null or empty");
        return serverPriceRepository.findRecentPriceByUniqueServerName(serverName)
                .map(serverPriceMapper::toPriceResponse)
                .orElseThrow(() -> new ServerPriceNotFoundException("No price found for server: " + serverName));
    }


    public ServerPriceResponse getPriceForServer(int serverId) {
        ValidationUtils.validatePositiveInt(serverId, "Server id must be a positive integer");
        return serverPriceRepository.findRecentPriceByServerId(serverId)
                .map(serverPriceMapper::toPriceResponse)
                .orElseThrow(() -> new ServerPriceNotFoundException("No price found for server: " + serverId));
    }


    public ServerPriceResponse getPriceForServer(Server server) {
        Objects.requireNonNull(server, "Server must not be null");
        return serverPriceRepository.findMostRecentPriceByServer(server)
                .map(serverPriceMapper::toPriceResponse)
                .orElseThrow(() -> new ServerPriceNotFoundException("No price found for server: " + server.getName()));
    }


    @Transactional
    public void savePrice(int serverId, ServerPriceResponse recentPrice) {
        Objects.requireNonNull(recentPrice, "Recent server price must not be null");
        ValidationUtils.validatePositiveInt(serverId, "Server id must be a positive integer");
        Server server = serverServiceImpl.getServer(serverId);
        ServerPrice serverPrice = serverPriceMapper.toServerPrice(recentPrice);
        serverPrice.setServer(server);
    }


    public ServerPriceResponse getAvgPriceForRegion(Region region) {
        Objects.requireNonNull(region, "Region must not be null");
        BigDecimal price = serverPriceRepository.findAvgPriceByRegion(region, AVERAGE_TIME_PERIOD)
                .orElseThrow(() -> new ServerPriceNotFoundException("No avg price found for region: " + region.name()));
        return serverPriceMapper.toPriceResponse(price, Currency.USD);
    }


    public List<ServerPriceResponse> getAllPricesForRegion(Region region, Pageable pageable) {
        Objects.requireNonNull(region, "Region must not be null");
        Objects.requireNonNull(pageable, "Pageable parameter must not be null");
        List<ServerPrice> prices = serverPriceRepository.findAllPricesByRegion(region, pageable).getContent();
        ValidationUtils.validateListNotEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found for region: " + region.name()));
        return serverPriceMapper.toPriceResponseList(prices);
    }



    public List<ServerPriceResponse> getAllPricesForFaction(Faction faction, Pageable pageable) {
        Objects.requireNonNull(faction, "Faction must not be null");
        Objects.requireNonNull(pageable, "Pageable parameter must not be null");
        List<ServerPrice> prices = serverPriceRepository.findAllPricesByFaction(faction, pageable).getContent();
        ValidationUtils.validateListNotEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found for faction: " + faction.name()));
        return serverPriceMapper.toPriceResponseList(prices);
    }


    public ServerPriceResponse getAvgPriceForFaction(Faction faction) {
        Objects.requireNonNull(faction, "Faction must not be null");
        BigDecimal price = serverPriceRepository.findAvgPriceByFaction(faction, AVERAGE_TIME_PERIOD)
                .orElseThrow(() -> new ServerPriceNotFoundException("No avg price found for faction: " + faction.name()));
        return serverPriceMapper.toPriceResponse(price, Currency.USD);
    }


    public List<ServerPriceResponse> getAllPricesForServer(String serverName, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable parameter cannot be null");
        ValidationUtils.validateNonEmptyString(serverName, "Server name must not be null or empty");
        List<ServerPrice> prices = serverPriceRepository.findAllByUniqueServerName(serverName, pageable).getContent();
        ValidationUtils.validateListNotEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found for server: " + serverName));
        return serverPriceMapper.toPriceResponseList(prices);
    }


    public List<ServerPriceResponse> getAllPricesForServer(int serverId, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable parameter cannot be null");
        ValidationUtils.validatePositiveInt(serverId, "Server id must be a positive integer");
        List<ServerPrice> prices = serverPriceRepository.findAllPricesByServerId(serverId, pageable).getContent();
        ValidationUtils.validateListNotEmpty(prices,
                () -> new ServerPriceNotFoundException("No prices found for server ID: " + serverId));
        return serverPriceMapper.toPriceResponseList(prices);
    }



    public ServerPriceResponse getAvgPriceForServer(String serverName) {
        ValidationUtils.validateNonEmptyString(serverName, "Server name must not be null or empty");
        BigDecimal price = serverPriceRepository.findAvgPriceByUniqueServerName(serverName, AVERAGE_TIME_PERIOD)
                .orElseThrow(() -> new ServerPriceNotFoundException("No avg price found for server: " + serverName));
        return serverPriceMapper.toPriceResponse(price, Currency.USD);
    }


    public ServerPriceResponse getAvgPriceForServer(int serverId) {
        ValidationUtils.validatePositiveInt(serverId,  "Server id must be a positive integer");
        BigDecimal price = serverPriceRepository.findAvgPriceByServerId(serverId, AVERAGE_TIME_PERIOD)
                .orElseThrow(() -> new ServerPriceNotFoundException("No avg price found for server ID: " + serverId));
        return serverPriceMapper.toPriceResponse(price, Currency.USD);
    }


    public LocalDateTime getAVERAGE_TIME_PERIOD() {
        return AVERAGE_TIME_PERIOD;
    }
}