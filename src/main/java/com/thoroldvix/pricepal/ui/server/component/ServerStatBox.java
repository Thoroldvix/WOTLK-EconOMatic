package com.thoroldvix.pricepal.ui.server.component;

import com.thoroldvix.pricepal.common.RequestDto;
import com.thoroldvix.pricepal.common.SearchCriteria;
import com.thoroldvix.pricepal.server.api.ServerPriceController;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.dto.StatisticsResponse;
import com.thoroldvix.pricepal.ui.server.renderer.ServerPriceRenderer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ServerStatBox extends VerticalLayout {
    private final ServerPriceController serverPriceController;
    private final ServerResponse server;
    private final H1 header = new H1();


    public ServerStatBox(ServerResponse server, ServerPriceController serverPriceController) {
        this.serverPriceController = serverPriceController;

        this.server = server;
        configureHeader();
        setWidthFull();
        getStyle().set("background", "#383633");
        setAlignItems(Alignment.START);

        HorizontalLayout serverStatLayout = new HorizontalLayout();
        serverStatLayout.setAlignItems(Alignment.CENTER);
        serverStatLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        serverStatLayout.add(getServerStatNames(), getServerStatValues());

        add(header, serverStatLayout);
    }

    private VerticalLayout getServerStatValues() {
        VerticalLayout serverStatValues = new VerticalLayout();
        serverStatValues.setAlignItems(Alignment.START);
        Span lastUpdated = getLastUpdatedValue();
        ServerPriceRenderer currentPriceValue = getCurrentPriceValue();
        ServerPriceRenderer avgPriceValue = getAvgPriceValue();
        ServerPriceRenderer avgRegionPriceValue = getServerRegionAvgPriceValue();


        serverStatValues.add(lastUpdated, currentPriceValue, avgPriceValue, avgRegionPriceValue);
        return serverStatValues;
    }

    private VerticalLayout getServerStatNames() {
        VerticalLayout serverStatNames = new VerticalLayout();
        Span span = new Span("Avg region price");
        span.getStyle().set("white-space", "nowrap");
        serverStatNames.add(new Span("Server"),
                new Span("Current price"),
                new Span("Avg price"),
                span);
        serverStatNames.setWidthFull();
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
        ServerPriceResponse recentPrice = getMostRecentPriceForServer(server);
        return new ServerPriceRenderer(recentPrice);
    }

    private ServerPriceRenderer getAvgPriceValue() {
        BigDecimal averagePrice =  getStatisticsForServer(server).average();
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .price(averagePrice)
                .build();
        return new ServerPriceRenderer(serverPriceResponse);
    }

    private StatisticsResponse getStatisticsForServer(ServerResponse server) {
       return serverPriceController.getStatisticsForServer(server.uniqueName()).getBody();
    }
    private ServerPriceResponse getMostRecentPriceForServer(ServerResponse server) {
        return serverPriceController.getPricesForServer(server.uniqueName(), 0, 1, "updatedAt,desc")
                .getBody()
                .get(0);
    }


    private ServerPriceRenderer getServerRegionAvgPriceValue() {
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .column("region")
                .operation(SearchCriteria.Operation.EQUALS)
                .joinTable("server")
                .joinOperation(true)
                .value(server.region().name())
                .build();

        BigDecimal regionAveragePrice = Objects.requireNonNull(serverPriceController
                .getStatistics(new RequestDto(List.of(searchCriteria))).getBody()).average();
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .price(regionAveragePrice)
                .build();
        return new ServerPriceRenderer(serverPriceResponse);
    }

    private Span getLastUpdatedValue() {
        ServerPriceResponse recentPrice = getMostRecentPriceForServer(server);
        return new Span(getLastUpdatedText(recentPrice.updatedAt()));
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
