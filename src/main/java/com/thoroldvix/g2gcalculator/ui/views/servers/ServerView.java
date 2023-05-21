package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.price.PriceController;
import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.ServerController;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.ui.views.AppHeader;
import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.List;

@Route(value = "wow-classic/server", layout = MainLayout.class)
@UIScope
@SpringComponent
public class ServerView extends VerticalLayout {
    private final PriceController priceController;
    private final ServerController serverController;
    private HorizontalLayout serverDetailsLayout;
    private AppHeader appHeader = new AppHeader();

    private ServerResponse server;
    private List<PriceResponse> prices;


    public ServerView(PriceController priceController,  ServerController serverController) {
        this.priceController = priceController;
        this.serverController = serverController;
        setPadding(false);



        add(appHeader);
    }

    private List<PriceResponse> getPrices(String serverName) {
        return priceController.getAllPricesForServer(serverName).getBody();
    }

    public void setup(String serverName) {
        removeAll();
        prices = getPrices(serverName);

        server = serverController.getServer(serverName).getBody();
        VaadinSession.getCurrent().setAttribute("selectedServer", server);

        configureHeader();
        configureServerDetailsLayout();
        configureServerInfo(server);
    }

    private void configureHeader() {
        appHeader = new AppHeader();
        Span serverName = new Span(server.name());
        serverName.getStyle().set("font-size", "var(--lumo-font-size-xxl)");
        serverName.getStyle().set("font-weight", "bold");

        HorizontalLayout layout = new HorizontalLayout(serverName, getFactionDisplay(server.faction()));
        layout.setPadding(true);
        appHeader.add(layout);

        add(appHeader);
    }

    private void configureServerInfo(ServerResponse server) {
        ServerOverviewBox serverOverviewBox = new ServerOverviewBox(server);
        ServerStatBox serverStatBox = new ServerStatBox(server, priceController);

        serverDetailsLayout.add(serverOverviewBox, serverStatBox);
    }

    private FactionRenderer getFactionDisplay(Faction faction) {
        FactionRenderer factionRenderer = new FactionRenderer(faction);


        factionRenderer.getStyle().set("font-weight", "bold");
        factionRenderer.getStyle().set("font-size", "var(--lumo-font-size-xxl)");
        return factionRenderer;
    }

    private void configureServerDetailsLayout() {
        serverDetailsLayout = new HorizontalLayout();




        HorizontalLayout layout = new HorizontalLayout(serverDetailsLayout);
        layout.setWidthFull();
        layout.getStyle().set("border-bottom", "1px solid #80808059");
        layout.setPadding(true);
        add(layout);
    }
}