package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemService;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
        itemGrid.addColumn(new ComponentRenderer<>(ItemGridView::getItemNameComponent))
                .setHeader("Name")
                .setSortable(true)
                .setWidth("250px");
        itemGrid.addColumn(new ComponentRenderer<>(itemInfo -> getPriceComponent(itemInfo.minBuyout()))).setHeader("Min Buyout")
                .setSortable(true)
                .setAutoWidth(true);
        itemGrid.addColumn(new ComponentRenderer<>(itemInfo -> getPriceComponent(itemInfo.marketValue()))).setHeader("Market Value")
                .setSortable(true)
                .setAutoWidth(true);
        itemGrid.addColumn(ItemInfo::quantity).setHeader("Market Quantity")
                .setSortable(true)
                .setAutoWidth(true);
        itemGrid.addColumn(ItemInfo::numAuctions).setHeader("Number of Auctions")
                .setSortable(true)
                .setAutoWidth(true);
        itemGrid.addColumn(ItemInfo::type).setHeader("Type")
                .setSortable(true)
                .setAutoWidth(true);
        itemGrid.addColumn(ItemInfo::quality).setHeader("Quality")
                .setSortable(true)
                .setAutoWidth(true);

    }

    private static HorizontalLayout getItemNameComponent(ItemInfo itemInfo) {
        Image itemIcon = new Image(itemInfo.icon(), "icon");
        itemIcon.setWidth("20px");
        Span name = new Span(itemInfo.name());
        HorizontalLayout layout = new HorizontalLayout(itemIcon, name);
        layout.setAlignItems(Alignment.CENTER);
        layout.setWidthFull();
        return layout;
    }


    private Component getPriceComponent(long value) {
        Image goldImage = new Image("images/gold_coin.png", "gold");
        goldImage.setWidth("20px");
        Image silverImage = new Image("images/silver_coin.png", "silver");
        silverImage.setWidth("20px");
        Image copperImage = new Image("images/copper_coin.png", "copper");
        copperImage.setWidth("20px");

        long gold = value / 10000;
        long silver = (value - gold * 10000) / 100;
        long copper = value - gold * 10000 - silver * 100;



        HorizontalLayout goldLayout = new HorizontalLayout(new Text(String.valueOf(gold)), goldImage);
        goldLayout.setAlignItems(Alignment.CENTER);
        goldLayout.setSpacing(false);
        goldLayout.getThemeList().add("spacing-xs");
        HorizontalLayout  silverLayout = new HorizontalLayout(new Text(String.valueOf(silver)), silverImage);
        silverLayout.setAlignItems(Alignment.CENTER);
        silverLayout.setSpacing(false);
        silverLayout.getThemeList().add("spacing-xs");
        HorizontalLayout copperLayout = new HorizontalLayout(new Text(String.valueOf(copper)), copperImage);
        copperLayout.setAlignItems(Alignment.CENTER);
        copperLayout.setSpacing(false);
        copperLayout.getThemeList().add("spacing-xs");

        if (gold == 0) {
            goldLayout.setVisible(false);
        }
        if (silver == 0) {
            silverLayout.setVisible(false);
        }
        if (copper == 0) {
            copperLayout.setVisible(false);
        }
        HorizontalLayout layout = new HorizontalLayout(goldLayout, silverLayout, copperLayout);
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }
}
