package com.thoroldvix.g2gcalculator.ui.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class AppHeader extends HorizontalLayout {

    public AppHeader() {
        setHeight("300px");
        addClassName("header");
        setPadding(false);

        setWidthFull();


        setAlignItems(Alignment.START);
        setDefaultVerticalComponentAlignment(Alignment.END);
    }
}
