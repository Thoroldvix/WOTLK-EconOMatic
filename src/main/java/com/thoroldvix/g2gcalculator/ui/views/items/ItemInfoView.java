package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemStats;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@CssImport("./styles/shared-styles.css")
@SpringComponent
@UIScope
public class ItemInfoView extends VerticalLayout {

    public ItemInfoView() {
        addClassName("bordered-layout");
        setVisible(false);
        setWidth("300px");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

      public void updateItemInfo(ItemStats itemStats) {
        setVisible(true);
        removeAll();
        Span name = new Span("Name: " + itemStats.name());
        Span minBuyout = new Span("Min buyout: " + formatPrice(itemStats.minBuyout()));
        Span marketValue = new Span("Market value: " + formatPrice(itemStats.marketValue()));
        Span quantity = new Span("Quantity: " + itemStats.quantity());

        add(name, minBuyout,  marketValue, quantity);
    }

     private String formatPrice(long value) {
        long gold = value / 10000;
        long silver = (value - gold * 10000) / 100;
        long copper = value - gold * 10000 - silver * 100;
        return gold + "g " + silver + "s " + copper + "c";
    }
}