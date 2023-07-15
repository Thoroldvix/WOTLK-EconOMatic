package com.thoroldvix.economatic.recommendation;

import com.thoroldvix.economatic.goldprice.GoldPriceRequest;
import com.thoroldvix.economatic.goldprice.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.GoldPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoldPriceScoreProvider extends ScoreProvider {
    private static final BigDecimal MAX_GOLD_PRICE_USD = new BigDecimal("0.0025");
    private final GoldPriceService goldPriceServiceImpl;
    private final RecommendationProp prop;


    public Map<String, BigDecimal> getGoldPriceScores(BigDecimal goldPriceWeight, Set<String> servers) {
        BigDecimal weight = getWeightOrDefault(goldPriceWeight, prop.goldPriceDefaultWeight());
        GoldPriceRequest request = new GoldPriceRequest(servers);

        List<GoldPriceResponse> recentPrices = goldPriceServiceImpl.getRecentForServerList(request).prices();
        return createScores(recentPrices, weight);
    }

    private Map<String, BigDecimal> createScores(List<GoldPriceResponse> recentPrices, BigDecimal weight) {
        return recentPrices.stream()
                .collect(Collectors.toMap(
                        GoldPriceResponse::server,
                        goldPriceResponse ->
                                calculateWeightedValue(goldPriceResponse.price(), weight, MAX_GOLD_PRICE_USD)
                ));
    }


}
