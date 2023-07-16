package com.thoroldvix.economatic.recommendation;

import com.thoroldvix.economatic.population.PopulationResponse;
import com.thoroldvix.economatic.population.PopulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
class PopulationScoreProvider extends ScoreProvider {

    private static final BigDecimal MAX_POPULATION = new BigDecimal("20000");
    private final PopulationService populationServiceImpl;
    private final BigDecimal populationDefaultWeight;
    private final int minAllowedPopulation;

    @Autowired
    public PopulationScoreProvider(PopulationService populationServiceImpl, RecommendationProp prop) {
        this.populationServiceImpl = populationServiceImpl;
        this.populationDefaultWeight = prop.populationDefaultWeight();
        this.minAllowedPopulation = prop.minAllowedPopulation();
    }

    public Map<String, BigDecimal> getPopulationScores(BigDecimal populationWeight) {
        BigDecimal weight = getWeightOrDefault(populationWeight, populationDefaultWeight);
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
        return population.value() >= minAllowedPopulation;
    }

}
