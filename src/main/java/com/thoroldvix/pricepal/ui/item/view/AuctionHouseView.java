package com.thoroldvix.pricepal.ui.item.view;

import com.thoroldvix.pricepal.ui.MainLayout;
import com.thoroldvix.pricepal.ui.item.component.ItemGrid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "wow-classic/auction-house", layout = MainLayout.class)
@PageTitle("Auction House")
public class AuctionHouseView extends VerticalLayout {

    public AuctionHouseView(ItemGrid itemGrid) {
        add(itemGrid);
    }
}