package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.price.PriceService;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "wow-classic/servers", layout = MainLayout.class)
public class ServerView extends VerticalLayout implements HasUrlParameter<String> {
    private final PriceService priceServiceImpl;
    private final ServerService serverServiceImpl;

    private final HorizontalLayout serverDetailsLayout = new HorizontalLayout();

    private List<PriceResponse> prices;



    public ServerView(PriceService priceServiceImpl, ServerService serverServiceImpl) {
        this.priceServiceImpl = priceServiceImpl;
        this.serverServiceImpl = serverServiceImpl;
        addClassName("server-view");
        setDefaultHorizontalComponentAlignment(Alignment.START);
        getStyle().set("padding", "20px");

        serverDetailsLayout.setAlignItems(Alignment.CENTER);
        serverDetailsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        serverDetailsLayout.setWidth("800px");


        add(serverDetailsLayout);

    }

    private List<PriceResponse> getPrices(String serverName) {
        return priceServiceImpl.getAllPricesForServer(serverName);
    }

    public void setup(String serverName) {
        prices = getPrices(serverName);
        ServerResponse server = serverServiceImpl.getServerResponse(serverName);
        configureServerInfo(server);

    }

    private void configureServerInfo(ServerResponse server) {
        ServerOverviewBox serverOverviewBox = new ServerOverviewBox(server);
        ServerStatBox serverStatBox = new ServerStatBox(server);
        serverDetailsLayout.add(serverOverviewBox, serverStatBox);
    }




    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        setup(parameter);
    }
}