package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemStats;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;

@CssImport("./styles/shared-styles.css")
@SpringComponent
@UIScope
@Getter
public class ItemOverview extends VerticalLayout {

    private VerticalLayout itemInfo;

    private HorizontalLayout header;

    public ItemOverview() {
        configureHeader();
        configureItemInfo();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.START);
        setSpacing(true);

    }

    private void configureItemInfo() {
        itemInfo = new VerticalLayout();

        itemInfo.addClassName("item-info-view");
        itemInfo.setVisible(false);
        itemInfo.setPadding(true);
        itemInfo.setWidth("auto");

        itemInfo.setAlignItems(Alignment.START);
        itemInfo.setJustifyContentMode(JustifyContentMode.CENTER);

        add(itemInfo);
    }

    private void configureHeader() {
        header = new HorizontalLayout();
        header.setVisible(false);
        header.setSpacing(true);
        header.setPadding(true);
        header.setAlignItems(Alignment.BASELINE);
        header.addClassName("item-overview-header");
        header.setWidth("300px");

        add(header);
    }

    private void updateHeader(String name, String icon) {
        header.removeAll();

        Span itemName = new Span(name.toUpperCase());
        Image itemIcon = new Image(icon, "icon");
        itemIcon.setHeight(itemName.getHeight());


        header.add(itemIcon, itemName);
        header.setVisible(true);
    }

    public void updateItemInfo(ItemStats itemStats) {
        itemInfo.setVisible(true);
        itemInfo.removeAll();

        HorizontalLayout itemInfoHeader = new HorizontalLayout();
        itemInfoHeader.addClassName("item-info-header");
        itemInfoHeader.setSpacing(true);
        itemInfoHeader.add(new Icon(VaadinIcon.BULLETS));
        itemInfoHeader.add(new Span("ITEM STATS"));

        addItemStats(itemStats, itemInfoHeader);

        updateHeader(itemStats.name(), itemStats.icon());
    }

    private void addItemStats(ItemStats itemStats, HorizontalLayout itemInfoHeader) {
        Span minBuyout = new Span("Min buyout " + formatPrice(itemStats.minBuyout()));
        minBuyout.addClassName("item-stat");
        minBuyout.setWidthFull();

        Span marketValue = new Span("Market value " + formatPrice(itemStats.marketValue()));
        marketValue.addClassName("item-stat");
        marketValue.setWidthFull();

        Span quantity = new Span("Quantity " + itemStats.quantity());
        quantity.addClassName("item-stat");
        quantity.setWidthFull();

        itemInfo.add(itemInfoHeader,  minBuyout, marketValue, quantity);
    }

    private String formatPrice(long value) {
        long gold = value / 10000;
        long silver = (value - gold * 10000) / 100;
        long copper = value - gold * 10000 - silver * 100;
        return gold + "g " + silver + "s " + copper + "c";
    }
}