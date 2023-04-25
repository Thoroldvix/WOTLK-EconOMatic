package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route(value = "wow-classic/items", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@SpringComponent
@UIScope
public class ItemsView extends VerticalLayout {
    private final ItemSearchView itemSearchView;
    private final ItemOverview itemOverview;

    public ItemsView(
            ItemSearchView itemSearchView,
            ItemOverview itemOverview) {

        this.itemSearchView = itemSearchView;
        this.itemOverview = itemOverview;


        addClassName("items-view");
        setSizeFull();
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        HorizontalLayout horizontalLayout = new HorizontalLayout(itemOverview);
        horizontalLayout.setWidth("100%");
        horizontalLayout.setPadding(false);
        horizontalLayout.setSpacing(false);
        horizontalLayout.setAlignItems(Alignment.START);


        add(itemSearchView, horizontalLayout);
    }


}