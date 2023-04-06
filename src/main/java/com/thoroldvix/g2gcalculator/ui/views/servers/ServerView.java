package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.storedobject.chart.SOChart;
import com.sun.jna.platform.win32.COM.IShellFolder;
import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.price.PriceService;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.List;

import static com.thoroldvix.g2gcalculator.ui.views.util.LineChartFactory.*;

@Route(value = "server")
public class ServerView extends VerticalLayout implements HasUrlParameter<Integer> {
    private final PriceService priceServiceImpl;
    private final ServerService serverServiceImpl;

    private List<PriceResponse> prices;



    public ServerView(PriceService priceServiceImpl, ServerService serverServiceImpl) {
        this.priceServiceImpl = priceServiceImpl;
        this.serverServiceImpl = serverServiceImpl;
        addClassName("server-view");
        setSizeFull();

    }

    private List<PriceResponse> getPrices(int id) {
        return priceServiceImpl.getAllPricesForServer(id);
    }

    public void setup(int id) {
        prices = getPrices(id);
        ServerResponse server = serverServiceImpl.getServerResponseById(id);
        configureServerInfo(server);
        configureCharts();
    }

    private void configureServerInfo(ServerResponse server) {
        Span serverNameSpan = new Span("Server: " + server.name());
        Span factionSpan = new Span("Faction: " + server.faction());
        serverNameSpan.addClassNames("text-xl", "mt-m");
        factionSpan.addClassNames("text-xl", "mt-m");

        VerticalLayout verticalLayout = new VerticalLayout(serverNameSpan, factionSpan);

        add(verticalLayout);
    }

    private void configureCharts() {
        SOChart soChart = new SOChart();
        soChart.setSize("1000px", "500px");
        soChart.add(getOneHourChart(prices));
        VerticalLayout verticalLayout = new VerticalLayout(soChart);
        verticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        add(verticalLayout);
    }


    @Override
    public void setParameter(BeforeEvent event, Integer parameter) {
        setup(parameter);
    }
}