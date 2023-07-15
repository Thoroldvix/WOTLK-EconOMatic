package com.thoroldvix.economatic.recommendation;

import com.thoroldvix.economatic.population.PopulationResponse;
import com.thoroldvix.economatic.population.PopulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class PopulationScoreProvider extends ScoreProvider {

    private static final BigDecimal MAX_POPULATION = new BigDecimal("20000");
    private final PopulationService populationServiceImpl;
    private final RecommendationProp prop;

   public Map<String, BigDecimal> getPopulationScores(BigDecimal populationWeight) {
        BigDecimal weight = getWeightOrDefault(populationWeight, prop.populationDefaultWeight());
       List<PopulationResponse> recentPopulations = populationServiceImpl.getAllRecent().populations();
       return createScores(recentPopulations, weight);
    }

    private Map<String, BigDecimal> createScores(List<PopulationResponse> recentPopulations, BigDecimal weight) {
        return recentPopulations.stream()
                .filter(this::filterLowPopulations)
                .collect(Collectors.toMap(
                        PopulationResponse::server,
                        populationResponse -> calculateWeightedValue(populationResponse.value(), weight, MAX_POPULATION)
                ));
    }


    private boolean filterLowPopulations(PopulationResponse population) {
        return population.value() >= this.prop.minAllowedPopulation();
    }


}
