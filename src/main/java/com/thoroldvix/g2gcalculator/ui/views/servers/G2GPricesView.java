package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "wow-classic/g2g-prices", layout = MainLayout.class)
@PageTitle("G2G Prices")
public class G2GPricesView extends VerticalLayout {

    private final ServerGridLayout serverGridLayout;

    public G2GPricesView(ServerGridLayout serverGridLayout) {
        this.serverGridLayout = serverGridLayout;
        setPadding(false);
        setSizeFull();

        add(serverGridLayout);
    }
}
