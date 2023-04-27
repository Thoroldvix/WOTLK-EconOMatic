package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemService;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.vaadin.klaudeta.PaginatedGrid;

@SpringComponent
@UIScope
public class ItemGridView extends VerticalLayout {
    private final ItemService itemServiceImpl;
    private final PaginatedGrid<ItemInfo, String> itemGrid = new PaginatedGrid<>();

    public ItemGridView(ItemService itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;

        addClassName("item-grid-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        configureGrid();
        add(itemGrid);
    }

    private void configureGrid() {
        itemGrid.setVisible(false);
        itemGrid.setPaginationVisibility(false);
        itemGrid.addClassName("item-grid");
        itemGrid.setSizeFull();
        itemGrid.setPageSize(20);
        itemGrid.setPaginatorSize(5);
        itemGrid.setPage(1);
        configureColumns();
    }

    public void updateGrid(String serverName) {
        itemGrid.setItems(itemServiceImpl.getAllItemsInfo(serverName));
        itemGrid.setVisible(true);
         itemGrid.setPaginationVisibility(true);
    }

    private void configureColumns() {
        itemGrid.addColumn(ItemInfo::name).setHeader("Name")
                .setSortable(true);
        itemGrid.addColumn(itemStats -> formatPrice(itemStats.minBuyout())).setHeader("Min Buyout")
                .setSortable(true);
        itemGrid.addColumn(itemStats -> formatPrice(itemStats.marketValue())).setHeader("Market Value")
                .setSortable(true);
        itemGrid.addColumn(ItemInfo::quantity).setHeader("Quantity")
                .setSortable(true);
        itemGrid.addColumn(ItemInfo::type).setHeader("Type")
                .setSortable(true);
        itemGrid.addColumn(ItemInfo::quality).setHeader("Quality")
                .setSortable(true);

    }


    private String formatPrice(long value) {
        long gold = value / 10000;
        long silver = (value - gold * 10000) / 100;
        long copper = value - gold * 10000 - silver * 100;
        return gold + "g " + silver + "s " + copper + "c";
    }
}
