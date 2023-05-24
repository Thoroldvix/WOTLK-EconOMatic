package com.thoroldvix.pricepal.ui.server.view;

import com.thoroldvix.pricepal.server.api.PriceController;
import com.thoroldvix.pricepal.server.api.ServerController;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.ui.AppHeader;
import com.thoroldvix.pricepal.ui.MainLayout;
import com.thoroldvix.pricepal.ui.server.component.ServerOverviewBox;
import com.thoroldvix.pricepal.ui.server.component.ServerStatBox;
import com.thoroldvix.pricepal.ui.server.renderer.FactionRenderer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.data.domain.PageRequest;

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
    private List<ServerPriceResponse> prices;


    public ServerView(PriceController priceController,  ServerController serverController) {
        this.priceController = priceController;
        this.serverController = serverController;
        setPadding(false);



        add(appHeader);
    }

    private List<ServerPriceResponse> getPrices(String serverName) {
        return priceController.getAllPricesForServer(serverName, PageRequest.of(0, Integer.MAX_VALUE)).getBody();
    }

    public void setup(String serverName) {
        removeAll();
        prices = getPrices(serverName);

        server = serverController.getServer(serverName).getBody();
        VaadinSession.getCurrent().setAttribute("selectedServer", serverName);

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