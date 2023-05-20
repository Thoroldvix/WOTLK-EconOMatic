package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.price.PriceService;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class ServerStatBox extends VerticalLayout {
    private final PriceService priceServiceImpl;
    private final ServerResponse server;
    private final H1 header = new H1();


    public ServerStatBox(ServerResponse server, PriceService priceServiceImpl) {
        this.priceServiceImpl = priceServiceImpl;
        this.server = server;
        configureHeader();
        setSizeFull();
        getStyle().set("background", "#383633");
        setAlignItems(Alignment.START);

        HorizontalLayout serverStatLayout = new HorizontalLayout();
        serverStatLayout.setAlignItems(Alignment.START);
        serverStatLayout.add(getServerStatNames(), getServerStatValues());

        add(header, serverStatLayout);
    }

    private VerticalLayout getServerStatValues() {
        VerticalLayout serverStatValues = new VerticalLayout();
        serverStatValues.setAlignItems(Alignment.START);
        Span lastUpdated = getLastUpdatedValue();
        ServerPriceRenderer currentPriceValue = getCurrentPriceValue();
        ServerPriceRenderer avgPriceValue = getAvgPriceValue();
        ServerPriceRenderer avgReginPriceValue = getServerRegionAvgPriceValue();


        serverStatValues.add(lastUpdated, currentPriceValue, avgPriceValue, avgReginPriceValue);
        return serverStatValues;
    }

    private VerticalLayout getServerStatNames() {
        VerticalLayout serverStatNames = new VerticalLayout();
        serverStatNames.add(new Span("Server"),
                new Span("Current price"),
                new Span("Avg price"),
                new Span("Avg region price"));
        serverStatNames.setAlignItems(Alignment.START);


        return serverStatNames;
    }

    private void configureHeader() {
        Icon icon = VaadinIcon.ABACUS.create();
        icon.setSize("20px");
        icon.getStyle().set("margin-right", "5px");
        icon.getStyle().set("margin-bottom", "3px");

        Span headerText = new Span("Server stats");
        header.getStyle().set("font-size", "var(--lumo-font-size-m)");
        header.add(icon, headerText);
    }

    private ServerPriceRenderer getCurrentPriceValue() {
        return new ServerPriceRenderer(server.price().value());
    }

    private ServerPriceRenderer getAvgPriceValue() {
        BigDecimal averagePrice = priceServiceImpl.getAvgPriceForServer(server.getFullServerName());
        return new ServerPriceRenderer(averagePrice);
    }

    private ServerPriceRenderer getServerRegionAvgPriceValue() {
        BigDecimal regionAveragePrice = priceServiceImpl.getAvgPriceForRegion(server.region().getParentRegion());
        return new ServerPriceRenderer(regionAveragePrice);
    }

    private Span getLastUpdatedValue() {
        return new Span(getLastUpdatedText(server.price().updatedAt()));
    }


    private String getLastUpdatedText(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, now);

        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        }
        long hours = duration.toHours();
        if (hours < 24) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        }
        long days = duration.toDays();
        return days == 1 ? "1 day ago" : days + " days ago";
    }
}
