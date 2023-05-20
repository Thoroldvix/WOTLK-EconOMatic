package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.github.appreciated.apexcharts.ApexCharts;
import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class ServerOverviewBox extends VerticalLayout {

    private final HorizontalLayout serverInfoLayout = new HorizontalLayout();
    private final HorizontalLayout factionInfoLayout = new HorizontalLayout();
    private final HorizontalLayout populationInfoLayout = new HorizontalLayout();

    private final HorizontalLayout regionInfoLayout = new HorizontalLayout();

    private final ServerResponse server;

    private final H1 header = new H1();


    public ServerOverviewBox(ServerResponse server) {
        this.server = server;
        addClassName("server-overview-box");
        getThemeList().set("dark", true);


        configureHeader();
        configureServerInfoLayout();
        configureFactionInfo();
        configurePopulationInfoLayout();
        configureRegionInfoLayout();
        setWidth("30%");

        add(header, serverInfoLayout,
                factionInfoLayout, regionInfoLayout, populationInfoLayout);
    }

    private void configureFactionInfo() {
        factionInfoLayout.setAlignItems(Alignment.START);
        factionInfoLayout.addClassName("server-stat");


        factionInfoLayout.add(new Span("Faction"), getFactionDisplay(server.faction()));
    }

    private void configureRegionInfoLayout() {
        regionInfoLayout.setAlignItems(Alignment.START);
        regionInfoLayout.addClassName("server-stat");
        Span region = new Span(server.region().getParentRegion().name());
        region.getStyle().set("margin-left", "48px");

        regionInfoLayout.add(new Span("Region"), region);
    }


    private void configureHeader() {
        Icon icon = VaadinIcon.INFO_CIRCLE_O.create();
        icon.setSize("20px");
        icon.getStyle().set("padding-right", "5px");
        icon.getStyle().set("padding-bottom", "3px");
        header.getStyle().set("margin-bottom", "10px");
        header.getStyle().set("font-size", "var(--lumo-font-size-m)");
        Span headerText = new Span("Overview");

        header.add(icon, headerText);
    }

    private void configurePopulationInfoLayout() {
        populationInfoLayout.addClassName("server-stat");
        populationInfoLayout.setAlignItems(Alignment.START);


        if (server.population().popAlliance() == 0 && server.population().popHorde() == 0) {
            Span zeroPopulation = new Span("0");
            zeroPopulation.getStyle().set("margin-left", "25px");
            populationInfoLayout.add(new Span("Population"), zeroPopulation);
        } else {
            ApexCharts populationChart = PopulationChart.getChart(server);
            populationChart.setHeight("100px");
            populationInfoLayout.setHeight("60px");
            populationInfoLayout.add(new Span("Population"), populationChart);
        }
    }

    private void configureServerInfoLayout() {
        Span serverName = new Span(server.name());
        serverName.getStyle().set("margin-left", "54px");

        serverInfoLayout.setAlignItems(Alignment.START);
        serverInfoLayout.addClassName("server-stat");
        serverInfoLayout.add(new Span("Server"), serverName);
    }

    private FactionRenderer getFactionDisplay(Faction faction) {
        FactionRenderer factionRenderer = new FactionRenderer(faction);

        factionRenderer.getStyle().set("margin-left", "42px");
        return factionRenderer;
    }

}
