package com.thoroldvix.g2gcalculator.g2g;

import com.thoroldvix.g2gcalculator.common.NotFoundException;
import com.thoroldvix.g2gcalculator.price.Price;
import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.server.Server;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class G2GService {
    private final G2GPriceClient g2GPriceClient;
    @Value("${g2g.currency:USD}")
    private  String currency;


    @SneakyThrows
    public Price fetchServerPrice(Server server) {
        String regionId = server.getRegion().getG2gId();
        String serverName = formatServerName(server);

        G2GPriceResponse response = g2GPriceClient.getPrices(regionId, currency);

        PriceResponse price = findPriceInResponse(serverName, response);

        return Price.builder()
                .value(price.value().stripTrailingZeros())
                .currency(currency)
                .build();
    }

    private static PriceResponse findPriceInResponse(String serverName, G2GPriceResponse response) {
        return response.prices().stream().filter(result -> result.serverName().equals(serverName))
                .findFirst()
                .orElseThrow(() -> new G2GPriceNotFoundException("Price not found for server: " + serverName));
    }

    private String formatServerName(Server server) {
        return String.format("%s [%s] - %s", server.getName(),
                server.getRegion(), server.getFaction().toString());
    }
}