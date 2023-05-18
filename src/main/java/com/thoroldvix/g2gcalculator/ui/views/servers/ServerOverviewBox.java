package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.Region;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.ui.views.FactionRenderer;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("./styles/shared-styles.css")
public class ServerOverviewBox extends VerticalLayout {

    private final HorizontalLayout serverInfo = new HorizontalLayout();
    private final HorizontalLayout factionInfo = new HorizontalLayout();
    private final HorizontalLayout populationInfo = new HorizontalLayout();

    private final HorizontalLayout regionInfo = new HorizontalLayout();

    private final ServerResponse server;

    private final H1 header = new H1("Overview");


    public ServerOverviewBox(ServerResponse server) {
        this.server = server;
        addClassName("server-overview-box");
        getThemeList().set("dark", true);


        configureHeader();
        configureServerInfo();
        configureFactionInfo();
        configurePopulationInfo();
        configureRegionInfo();


        add(header, serverInfo,
                factionInfo, populationInfo, regionInfo);
    }

    private void configureFactionInfo() {
        factionInfo.setAlignItems(Alignment.START);
        factionInfo.addClassName("server-stat");


        factionInfo.add(new Span("Faction"), getFactionDisplay(server.faction()));
    }

    private void configureRegionInfo() {
        regionInfo.setAlignItems(Alignment.START);

        regionInfo.addClassName("server-stat");

        Span region = new Span(getRegion(server.region()));
        region.getStyle().set("margin-left", "55px");

        regionInfo.add(new Span("Region"), region);
    }

    private String getRegion(Region region) {
        return switch (region) {
            case EU -> Region.EU.name();
            case US -> Region.US.name();
            default -> String.format("%s (%s)", Region.getParentregion(region).name(), region.name());
        };

    }

    private void configureHeader() {
        header.getStyle().set("margin-bottom", "10px");
        header.getStyle().set("font-size", "var(--lumo-font-size-m)");
    }

    private void configurePopulationInfo() {
        populationInfo.addClassName("server-stat");

        populationInfo.setAlignItems(Alignment.START);


        populationInfo.add(new Span("Population"));
    }

    private void configureServerInfo() {
        Span serverName = new Span(server.name());
        serverName.getStyle().set("margin-left", "55px");

        serverInfo.setAlignItems(Alignment.START);
        serverInfo.addClassName("server-stat");
        serverInfo.add(new Span("Server"), serverName);
    }

    private FactionRenderer getFactionDisplay(Faction faction) {
        FactionRenderer factionRenderer = new FactionRenderer(faction);
        ;
        factionRenderer.getStyle().set("margin-left", "45px");
        return factionRenderer;
    }
}
