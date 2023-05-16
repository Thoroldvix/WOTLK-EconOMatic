package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route(value = "wow-classic/auction-house", layout = MainLayout.class)
@PageTitle("Auction House")
@SpringComponent
@UIScope
public class AuctionHouseView extends VerticalLayout {


    public AuctionHouseView(
            ItemGridLayout itemGridLayout) {

        addClassName("items-view");
        setSizeFull();
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        add(itemGridLayout);
    }


}