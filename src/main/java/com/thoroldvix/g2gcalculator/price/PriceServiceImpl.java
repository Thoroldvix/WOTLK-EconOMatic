package com.thoroldvix.g2gcalculator.price;

import com.thoroldvix.g2gcalculator.common.NotFoundException;
import com.thoroldvix.g2gcalculator.server.Server;
import com.thoroldvix.g2gcalculator.server.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public <T> PriceResponse getPriceForServer(T serverParam) {
        Objects.requireNonNull(serverParam, "Server must not be null");
        Class<?> clazz = serverParam.getClass();
        Server server;
        if (clazz.equals(String.class)) {
            server = serverServiceImpl.getServer((String) serverParam);
        } else if (clazz.equals(Server.class)) {
            server = (Server) serverParam;
        } else {
            throw new IllegalArgumentException("Server must be a valid server name or a Server object");
        }
        return priceRepository.findMostRecentPriceByServer(server).map(priceMapper::toPriceResponse)
                .orElseThrow(() -> new NotFoundException("No price found for server: " + server.getName()));
    }

    @Override
    @Transactional
    public void updatePrice(int serverId, PriceResponse recentPrice) {
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
}