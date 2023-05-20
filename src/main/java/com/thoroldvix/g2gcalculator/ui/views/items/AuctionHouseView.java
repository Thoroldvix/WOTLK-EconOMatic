package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "wow-classic/auction-house", layout = MainLayout.class)
@PageTitle("Auction House")
public class AuctionHouseView extends VerticalLayout {

    public AuctionHouseView(ItemGridLayout itemGridLayout) {
        setSizeFull();
        setPadding(false);
        setAlignItems(Alignment.CENTER);
        add(itemGridLayout);
    }
}