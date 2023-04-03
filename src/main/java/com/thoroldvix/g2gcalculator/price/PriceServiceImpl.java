package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.common.NotFoundException;
import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.server.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public List<PriceResponse> getAllPricesForServer(String serverName, Pageable pageable) {
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findAllByServer(server, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public List<PriceResponse> getAllPricesForServer(String serverName) {
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findAllByServer(server).stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public List<PriceResponse> getAllPricesForServer(int serverId) {
        Server server = serverServiceImpl.getServerById(serverId);
        return priceRepository.findAllByServer(server).stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }
}