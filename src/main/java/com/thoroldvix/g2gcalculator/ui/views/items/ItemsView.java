package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route(value = "wow-classic/items", layout = MainLayout.class)
@SpringComponent
@UIScope
public class ItemsView extends VerticalLayout {
    private final ItemSearchView itemSearchView;
    private final ItemInfoView itemInfo;

    public ItemsView(
            ItemSearchView itemSearchView,
            ItemInfoView itemInfo) {

        this.itemSearchView = itemSearchView;
        this.itemInfo = itemInfo;


        addClassName("items-view");
        setSizeFull();


        setAlignItems(Alignment.CENTER);


        add(itemSearchView, this.itemInfo);
    }


}