package com.thoroldvix.economatic.recommendation;

import java.math.BigDecimal;
import java.math.MathContext;

abstract class ScoreProvider {

    protected BigDecimal calculateWeightedValue(Number value, BigDecimal weight, BigDecimal maxWeight) {
        BigDecimal normalizedValue = BigDecimal.valueOf(value.doubleValue())
                .divide(maxWeight, MathContext.DECIMAL64);
        return normalizedValue.multiply(weight);
    }

    protected BigDecimal getWeightOrDefault(BigDecimal weight, BigDecimal defaultWeight) {
        return weight == null ? defaultWeight : weight;
    }
}
