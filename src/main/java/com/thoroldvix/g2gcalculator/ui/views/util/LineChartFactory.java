package com.thoroldvix.g2gcalculator.ui.views.util;

import com.storedobject.chart.*;
import com.thoroldvix.g2gcalculator.price.PriceResponse;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;

public class LineChartFactory {

    public static XYChart getOneHourChart(List<PriceResponse> prices) {
        return createChart(prices, LineChartFactory::filterOneHourPrices);
    }

    public static XYChart getThreeHourChart(List<PriceResponse> prices) {
        return createChart(prices, LineChartFactory::filterThreeHourPrices);
    }

    public static XYChart getOneDayChart(List<PriceResponse> prices) {
        return createChart(prices, LineChartFactory::filterOneDayPrices);
    }
     public static XYChart getOneWeekChart(List<PriceResponse> prices) {
        return createChart(prices, LineChartFactory::filterOneWeekPrices);
    }

    private static XYChart createChart(List<PriceResponse> prices, Predicate<PriceResponse> filterPredicate) {
        Data xValues = new Data();
        Data yValues = new Data();
        List<PriceResponse> filteredPrices = prices.stream()
                .filter(filterPredicate)
                .toList();

        filteredPrices.forEach(price -> {
            xValues.add(price.updatedAt().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
            yValues.add(price.value());
        });

        LineChart lineChart = new LineChart(xValues, yValues);
        XAxis xAxis = new XAxis(DataType.DATE);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);

        lineChart.plotOn(rc);


        return lineChart;
    }

    private static boolean filterOneHourPrices(PriceResponse p) {
        LocalDateTime priceTime = (p.updatedAt());
        LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        return priceTime.isBefore(currentHour) && priceTime.isAfter(currentHour.minusHours(1));
    }

    private static boolean filterThreeHourPrices(PriceResponse p) {
        LocalDateTime priceTime = (p.updatedAt());
        LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        return priceTime.isBefore(currentHour) && priceTime.isBefore(currentHour.minusHours(3));
    }

    private static boolean filterOneDayPrices(PriceResponse p) {
        LocalDateTime priceTime = p.updatedAt();
        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);
        return priceTime.isAfter(startOfDay) && priceTime.isBefore(startOfNextDay);
    }
     private static boolean filterOneWeekPrices(PriceResponse p) {
        LocalDateTime priceTime = p.updatedAt();
        LocalDateTime startOfDay = LocalDateTime.now();
        LocalDateTime startOfNextDay = startOfDay.plusDays(7);
        return priceTime.isAfter(startOfDay) && priceTime.isBefore(startOfNextDay);
    }


}