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
    private final ItemGridView  itemGridView;

    private final ServerSelectionView serverSelectionView;


    public ItemsView(
            ItemGridView itemGridView, ServerSelectionView serverSelectionView) {



        this.itemGridView = itemGridView;
        this.serverSelectionView = serverSelectionView;


        addClassName("items-view");
        setSizeFull();
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        add(serverSelectionView, itemGridView);
    }


}