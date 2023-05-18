package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.Region;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.ui.views.FactionRenderer;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("./styles/shared-styles.css")
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


        add(header, serverInfoLayout,
                factionInfoLayout, populationInfoLayout, regionInfoLayout);
    }

    private void configureFactionInfo() {
        factionInfoLayout.setAlignItems(Alignment.START);
        factionInfoLayout.addClassName("server-stat");


        factionInfoLayout.add(new Span("Faction"), getFactionDisplay(server.faction()));
    }

    private void configureRegionInfoLayout() {
        regionInfoLayout.setAlignItems(Alignment.START);

        regionInfoLayout.addClassName("server-stat");

        Span region = new Span(getRegion(server.region()));
        region.getStyle().set("margin-left", "55px");

        regionInfoLayout.add(new Span("Region"), region);
    }

    private String getRegion(Region region) {
        return switch (region) {
            case EU -> Region.EU.name();
            case US -> Region.US.name();
            default -> String.format("%s (%s)", region.getParentRegion().name(), region.name());
        };

    }

    private int getServerPopulation(ServerResponse server) {
        return server.faction() == Faction.ALLIANCE ? server.population().popAlliance() : server.population().popHorde();
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


        populationInfoLayout.add(new Span("Population"));
    }

    private void configureServerInfoLayout() {
        Span serverName = new Span(server.name());
        serverName.getStyle().set("margin-left", "55px");

        serverInfoLayout.setAlignItems(Alignment.START);
        serverInfoLayout.addClassName("server-stat");
        serverInfoLayout.add(new Span("Server"), serverName);
    }

    private FactionRenderer getFactionDisplay(Faction faction) {
        FactionRenderer factionRenderer = new FactionRenderer(faction);

        factionRenderer.getStyle().set("margin-left", "45px");
        return factionRenderer;
    }
}
