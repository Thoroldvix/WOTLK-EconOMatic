package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.api.G2GPriceClient;
import com.thoroldvix.g2gcalculator.dto.G2GApiResponse;
import com.thoroldvix.g2gcalculator.error.NotFoundException;
import com.thoroldvix.g2gcalculator.model.Price;
import com.thoroldvix.g2gcalculator.model.Realm;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class G2GService {
    private final G2GPriceClient g2GPriceClient;
    @Value("${g2g.currency:USD}")
    private  String currency;


    @SneakyThrows
    public Price fetchRealmPrice(Realm realm) {
        String regionId = realm.getRegion().getG2gId();
        String realmName = formatRealmName(realm);

        G2GApiResponse.Payload response = g2GPriceClient.getPriceForRealm(regionId, currency).payload();

        BigDecimal price = response.results().stream().filter(result -> result.realmName().equals(realmName))
                .findFirst().map(G2GApiResponse.Payload.G2GPriceResponse::price).orElseThrow(() -> new NotFoundException("No price found for realm " + realmName));

        return Price.builder()
                .updatedAt(LocalDateTime.now())
                .value(price)
                .currency(currency)
                .build();
    }

     private String formatRealmName(Realm realm) {
        return String.format("%s [%s] - %s", realm.getName(),
                realm.getRegion(), realm.getFaction().toString());
    }
}