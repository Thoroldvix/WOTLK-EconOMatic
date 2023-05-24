package com.thoroldvix.pricepal.ui.item.component;

import com.thoroldvix.pricepal.item.api.ItemPriceController;
import com.thoroldvix.pricepal.item.dto.FullAuctionHouseInfo;
import com.thoroldvix.pricepal.item.dto.FullItemInfo;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.ui.item.renderer.ItemNameRenderer;
import com.thoroldvix.pricepal.ui.item.renderer.ItemPriceRenderer;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SpringComponent
@UIScope
public class ItemGrid extends VerticalLayout {

    private final ItemPriceController itemPriceController;

    private final ItemFilter itemFilter;

    private final PaginatedGrid<FullItemInfo, ?> itemGrid = new PaginatedGrid<>();
    private String serverName;

    public ItemGrid( ItemPriceController itemPriceController) {
        this.itemPriceController = itemPriceController;
        setPadding(false);
        itemFilter = new ItemFilter(this);
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
        serverName = formatServerName(server);

        FullAuctionHouseInfo fullAuctionHouseInfo = (FullAuctionHouseInfo) itemPriceController.getAuctionHouseInfo(serverName, true).getBody();
        List<FullItemInfo> items = Objects.requireNonNull(fullAuctionHouseInfo).items();

        itemGrid.setItems(items);

        add(itemFilter, itemGrid);
    }



    public void onFilterChange(Predicate<FullItemInfo> filter) {
        FullAuctionHouseInfo fullAuctionHouseInfo = (FullAuctionHouseInfo) itemPriceController.getAuctionHouseInfo(serverName, true).getBody();
        Set<FullItemInfo> items = Objects.requireNonNull(fullAuctionHouseInfo).items().stream()
                .filter(filter).collect(Collectors.toSet());
        itemGrid.setItems(items);
    }

    private void configureColumnsForServer() {
        itemGrid.addColumn(new ComponentRenderer<>(item -> new ItemNameRenderer(item.itemInfo())))
                .setHeader("Name")
                .setComparator(Comparator.comparing(item -> item.itemInfo().name()));
        itemGrid.addColumn(new ComponentRenderer<>(item -> new ItemPriceRenderer(item.auctionInfo().minBuyout())))
                .setHeader("Min Buyout")
                .setComparator(Comparator.comparingLong(item -> item.auctionInfo().minBuyout()));
        itemGrid.addColumn(new ComponentRenderer<>(item -> new ItemPriceRenderer(item.auctionInfo().marketValue())))
                .setHeader("Market Value")
                .setSortable(true)
                .setComparator(Comparator.comparingLong(item -> item.auctionInfo().marketValue()));
        itemGrid.addColumn(item -> item.auctionInfo().quantity()).setHeader("Market Quantity");
        itemGrid.addColumn(item -> item.auctionInfo().numAuctions()).setHeader("Number of Auctions");

        itemGrid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setSortable(true);
        });

    }

    private String formatServerName(ServerResponse server) {
        return server.name().replaceAll("'", "").replaceAll(" ", "-")
                .toLowerCase() + "-" + server.faction().name().toLowerCase();
    }
}
