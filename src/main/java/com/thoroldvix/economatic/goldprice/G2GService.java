package com.thoroldvix.economatic.goldprice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class G2GService {

    private final G2GPriceClient g2gPriceClient;
    private final GoldPriceDeserializer goldPriceDeserializer;

    public List<GoldPriceResponse> getGoldPrices() {
        String pricesJson = g2gPriceClient.getAllPrices();
        return goldPriceDeserializer.extractPricesFromJson(pricesJson);
    }

}
