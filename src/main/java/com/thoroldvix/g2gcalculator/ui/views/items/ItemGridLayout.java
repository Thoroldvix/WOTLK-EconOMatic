package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemService;
import com.thoroldvix.g2gcalculator.item.ItemsController;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SpringComponent
@UIScope
public class ItemGridLayout extends VerticalLayout {
    private final ItemsController itemsController;

    private final ItemFilteringLayout itemFilteringLayout;

    private final PaginatedGrid<ItemInfo, ?> itemGrid = new PaginatedGrid<>();
    private String serverName;

    public ItemGridLayout(ItemService itemServiceImpl, ItemsController itemsController) {
        this.itemsController = itemsController;
        setPadding(false);
        itemFilteringLayout = new ItemFilteringLayout(this);
        configureColumnsForServer();
        configureGrid();
    }


    private void configureGrid() {
        itemGrid.setSelectionMode(Grid.SelectionMode.NONE);
        itemGrid.addClassName("item-grid");
        itemGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        itemGrid.setPageSize(20);
        itemGrid.setPaginatorSize(5);
        itemGrid.setPage(1);
    }

    public void populateGridForServer(ServerResponse server) {
        serverName = getFormattedServerName(server);
        Set<ItemInfo> allItemsInfo = itemsController.getAllItemsForServer(serverName);
        itemGrid.setItems(allItemsInfo);

        add(itemFilteringLayout, itemGrid);
    }


    public void onFilterChange(Predicate<ItemInfo> filter) {

        Set<ItemInfo> items = itemsController.getAllItemsForServer(serverName).stream()
                .filter(filter).collect(Collectors.toSet());
        itemGrid.setItems(items);
    }

    private void configureColumnsForServer() {
        itemGrid.addColumn(new ComponentRenderer<>(ItemNameRenderer::new))
                .setHeader("Name")
                .setComparator(Comparator.comparing(ItemInfo::name));
        itemGrid.addColumn(new ComponentRenderer<>(itemInfo -> new ItemPriceRenderer(itemInfo.auctionHouseInfo().minBuyout())))
                .setHeader("Min Buyout")
                .setComparator(Comparator.comparingLong(itemInfo -> itemInfo.auctionHouseInfo().minBuyout()));
        itemGrid.addColumn(new ComponentRenderer<>(itemInfo -> new ItemPriceRenderer(itemInfo.auctionHouseInfo().marketValue())))
                .setHeader("Market Value")
                .setSortable(true)
                .setComparator(Comparator.comparingLong(itemInfo -> itemInfo.auctionHouseInfo().marketValue()));
        itemGrid.addColumn(itemInfo -> itemInfo.auctionHouseInfo().quantity()).setHeader("Market Quantity");
        itemGrid.addColumn(itemInfo -> itemInfo.auctionHouseInfo().numAuctions()).setHeader("Number of Auctions");

        itemGrid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setSortable(true);
        });

    }

    private String getFormattedServerName(ServerResponse value) {
        return value.name().toLowerCase().replace(" ", "-") + "-"
               + value.faction().toString().toLowerCase();
    }
}
