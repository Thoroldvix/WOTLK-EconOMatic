package com.thoroldvix.pricepal.ui.server.view;

import com.thoroldvix.pricepal.ui.MainLayout;
import com.thoroldvix.pricepal.ui.server.component.ServerGrid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "wow-classic/g2g-prices", layout = MainLayout.class)
@PageTitle("G2G Prices")
public class G2GPricesView extends VerticalLayout {

    private final ServerGrid serverGrid;

    public G2GPricesView(ServerGrid serverGrid) {
        this.serverGrid = serverGrid;
        setPadding(false);
        setSizeFull();

        add(serverGrid);
    }
}
