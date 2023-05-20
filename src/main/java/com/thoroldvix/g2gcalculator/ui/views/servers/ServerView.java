package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.price.PriceService;
import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
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
    private final PriceService priceServiceImpl;
    private final ServerService serverServiceImpl;
    private HorizontalLayout serverDetailsLayout;
    private AppHeader appHeader = new AppHeader();

    private ServerResponse server;
    private List<PriceResponse> prices;


    public ServerView(PriceService priceServiceImpl, ServerService serverServiceImpl) {
        this.priceServiceImpl = priceServiceImpl;
        this.serverServiceImpl = serverServiceImpl;


        addClassName("server-view");
        setDefaultHorizontalComponentAlignment(Alignment.START);
        setPadding(false);
        add(appHeader);
    }

    private List<PriceResponse> getPrices(String serverName) {
        return priceServiceImpl.getAllPricesForServer(serverName);
    }

    public void setup(String serverName) {
        removeAll();
        prices = getPrices(serverName);

        server = serverServiceImpl.getServerResponse(serverName);
        VaadinSession.getCurrent().setAttribute("selectedServer", server);

        configureHeader();
        configureServerDetailsLayout();
        configureServerInfo(server);
    }

    private void configureHeader() {
        appHeader = new AppHeader();
        Span serverName = new Span(server.name());
        serverName.getStyle().set("font-size", "var(--lumo-font-size-xxl)");
        serverName.getStyle().set("margin-left", "30px");
        serverName.getStyle().set("font-weight", "bold");


        appHeader.add(serverName, getFactionDisplay(server.faction()));
        add(appHeader);
    }

    private void configureServerInfo(ServerResponse server) {
        ServerOverviewBox serverOverviewBox = new ServerOverviewBox(server);
        ServerStatBox serverStatBox = new ServerStatBox(server, priceServiceImpl);

        serverDetailsLayout.add(serverOverviewBox, serverStatBox);

    }

    private FactionRenderer getFactionDisplay(Faction faction) {
        FactionRenderer factionRenderer = new FactionRenderer(faction);

        factionRenderer.getStyle().set("margin-left", "5px");
        factionRenderer.getStyle().set("font-weight", "bold");
        factionRenderer.getStyle().set("font-size", "var(--lumo-font-size-xxl)");
        return factionRenderer;
    }

    private void configureServerDetailsLayout() {
        serverDetailsLayout = new HorizontalLayout();
        serverDetailsLayout.setAlignItems(Alignment.START);
        serverDetailsLayout.getStyle().set("border-bottom", "1px solid #80808059");
        serverDetailsLayout.getStyle().set("padding", "20px");

        serverDetailsLayout.setWidthFull();
        add(serverDetailsLayout);
    }
}