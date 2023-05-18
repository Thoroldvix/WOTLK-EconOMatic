package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.Duration;
import java.time.LocalDateTime;

public class ServerStatBox extends VerticalLayout {

    private final HorizontalLayout lastUpdatedLayout = new HorizontalLayout();

    private final HorizontalLayout currentPriceLayout = new HorizontalLayout();

    private final HorizontalLayout avgPriceLayout = new HorizontalLayout();

    private final HorizontalLayout regionAvgPriceLayout = new HorizontalLayout();

    private final H1 header = new H1();


    public ServerStatBox(ServerResponse server) {
        addClassName("server-stat-box");
        getStyle().set("margin-left", "50px");
        configureHeader();
        configureLastUpdatedLayout();
        configureCurrentPriceLayout();
        configureAvgPriceLayout();
        configureRegionAvgPriceLayout();

        add(header, lastUpdatedLayout, currentPriceLayout, avgPriceLayout, regionAvgPriceLayout);
    }

    private void configureHeader() {
        Icon icon = VaadinIcon.ABACUS.create();
        icon.setSize("20px");
        icon.getStyle().set("margin-right", "5px");
        icon.getStyle().set("margin-bottom", "3px");

        Span headerText = new Span("Server stats");
        header.getStyle().set("margin-bottom", "10px");
        header.getStyle().set("font-size", "var(--lumo-font-size-m)");
        header.add(icon, headerText);
    }

    private void configureLastUpdatedLayout() {
        Span lastUpdated = new Span(getLastUpdatedText(LocalDateTime.now().minusHours(1)));
        lastUpdatedLayout.addClassName("server-stat");
        lastUpdatedLayout.add(new Span("Last updated"), lastUpdated);
    }

    private void configureCurrentPriceLayout() {
        Span currentPrice = new Span("Current price");
        currentPriceLayout.addClassName("server-stat");
        currentPriceLayout.add(currentPrice);
    }

    private void configureAvgPriceLayout() {
        Span avgPrice = new Span("Average price");
        avgPriceLayout.addClassName("server-stat");
        avgPriceLayout.add(avgPrice);
    }

    private void configureRegionAvgPriceLayout() {
        Span regionAvgPrice = new Span("Region average price");
        regionAvgPriceLayout.addClassName("server-stat");
        regionAvgPriceLayout.add(regionAvgPrice);
    }

    private String getLastUpdatedText(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, now);

        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + " minutes ago";
        }

        long hours = duration.toHours();
        if (hours < 24) {
            return hours + " hours ago";
        }

        long days = duration.toDays();
        return days + " days ago";
    }
}
