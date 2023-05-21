package com.thoroldvix.g2gcalculator.ui.server.component;

import com.github.appreciated.apexcharts.ApexCharts;
import com.thoroldvix.g2gcalculator.server.dto.ServerResponse;
import com.thoroldvix.g2gcalculator.server.entity.Faction;
import com.thoroldvix.g2gcalculator.ui.server.chart.PopulationChart;
import com.thoroldvix.g2gcalculator.ui.server.renderer.FactionRenderer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class ServerOverviewBox extends VerticalLayout {

    private final ServerResponse server;

    private final H1 header = new H1();


    public ServerOverviewBox(ServerResponse server) {
        this.server = server;
        addClassName("server-overview-box");
        getThemeList().set("dark", true);

        configureHeader();

        getStyle().set("background", "#383633");


        HorizontalLayout serverInfoLayout = new HorizontalLayout();


        VerticalLayout valueNamesLayout = new VerticalLayout();
        valueNamesLayout.add(new Span("Server"),
                new Span("Faction"),
                new Span("Region"),
                new Span("Population"));


        serverInfoLayout.add(valueNamesLayout, getServerOverviewValues());
        add(header, serverInfoLayout);
    }


    private VerticalLayout getServerOverviewValues() {
        VerticalLayout serverOverviewLayout = new VerticalLayout();

        Span region = new Span(server.region().getParentRegion().name());
        serverOverviewLayout.add(getServerNameValue(), getFactionDisplay(server.faction()), region, getPopulationValue());

        return serverOverviewLayout;
    }


    private void configureHeader() {
        Icon icon = VaadinIcon.INFO_CIRCLE_O.create();
        icon.setSize("20px");
        icon.getStyle().set("padding-right", "5px");
        icon.getStyle().set("padding-bottom", "3px");


        header.getStyle().set("font-size", "var(--lumo-font-size-m)");
        Span headerText = new Span("Overview");

        header.add(icon, headerText);
    }

    private Component getPopulationValue() {
        HorizontalLayout populationLayout = new HorizontalLayout();

        populationLayout.setSizeFull();
        if (server.population().popAlliance() == 0 && server.population().popHorde() == 0) {
            populationLayout.add(new Span("0"));
            return populationLayout;
        }
        ApexCharts populationChart = PopulationChart.getChart(server);

        populationLayout.add(populationChart);
        return populationLayout;
    }

    private Span getServerNameValue() {
        return new Span(server.name());
    }

    private FactionRenderer getFactionDisplay(Faction faction) {
        return new FactionRenderer(faction);
    }

}
