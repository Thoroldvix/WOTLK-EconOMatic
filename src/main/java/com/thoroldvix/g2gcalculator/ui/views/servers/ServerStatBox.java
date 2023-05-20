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

    private final HorizontalLayout lastUpdatedLayout = new HorizontalLayout();

    private final HorizontalLayout currentPriceLayout = new HorizontalLayout();

    private final HorizontalLayout avgPriceLayout = new HorizontalLayout();

    private final HorizontalLayout regionAvgPriceLayout = new HorizontalLayout();

    private final PriceService priceServiceImpl;
    private final ServerResponse server;
    private final H1 header = new H1();


    public ServerStatBox(ServerResponse server, PriceService priceServiceImpl) {
        this.priceServiceImpl = priceServiceImpl;
        this.server = server;
        addClassName("server-stat-box");
        getStyle().set("margin-left", "50px");
        configureHeader();
        configureLastUpdatedLayout();
        configureCurrentPriceLayout();
        configureAvgPriceLayout();
        configureRegionAvgPriceLayout();
        setWidth("30%");
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
        lastUpdated.getStyle().set("margin-left", "28px");
        lastUpdatedLayout.addClassName("server-stat");
        lastUpdatedLayout.add(new Span("Last updated"), lastUpdated);
    }

    private void configureCurrentPriceLayout() {
        currentPriceLayout.addClassName("server-stat");
        ServerPriceRenderer serverPriceLayout = new ServerPriceRenderer(server.price().value());
        serverPriceLayout.getStyle().set("margin-left", "28px");
        currentPriceLayout.add(new Span("Current price"), serverPriceLayout);
    }

    private void configureAvgPriceLayout() {
        avgPriceLayout.addClassName("server-stat");
        BigDecimal averagePrice = priceServiceImpl.getAvgPriceForServer(server.getFullServerName());

        ServerPriceRenderer serverPriceLayout = new ServerPriceRenderer(averagePrice);
        serverPriceLayout.getStyle().set("margin-left", "53px");
        avgPriceLayout.add(new Span("Avg price"), serverPriceLayout);
    }

    private void configureRegionAvgPriceLayout() {
        BigDecimal regionAveragePrice = priceServiceImpl.getAvgPriceForRegion(server.region().getParentRegion());

        regionAvgPriceLayout.addClassName("server-stat");
        ServerPriceRenderer serverPriceLayout = new ServerPriceRenderer(regionAveragePrice);
        serverPriceLayout.getStyle().set("margin-left", "1px");
        regionAvgPriceLayout.add(new Span("Region avg price"), serverPriceLayout);
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
