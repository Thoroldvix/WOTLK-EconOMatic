package com.example.g2gcalculator.util;

import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.model.Price;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Duration;
import java.time.LocalDateTime;


public final class CalculatorUtils {

    private CalculatorUtils() { }

    public static boolean checkIfOld(Price recentPrice, Duration priceUpdateInterval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastUpdated = recentPrice.getUpdatedAt();
        return now.isAfter(lastUpdated.plus(priceUpdateInterval));
    }
}