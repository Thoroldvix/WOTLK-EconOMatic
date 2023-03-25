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


      public static String getExactRealmName(String realmName) {
        return realmName.split("-")[0];
    }


    public static Faction getFaction(String realmName) {
        String[] split = realmName.split("-");
        String faction = split[split.length - 1];
        if (split.length != 2 || !Faction.contains(faction)) {
            throw new NotFoundException("No faction found for name: " + faction);
        }
        return faction.equalsIgnoreCase("horde") ? Faction.HORDE : Faction.ALLIANCE;
    }

    public static boolean checkIfOld(Price recentPrice, Duration priceUpdateInterval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastUpdated = recentPrice.getUpdatedAt();
        return now.isAfter(lastUpdated.plus(priceUpdateInterval));
    }
}