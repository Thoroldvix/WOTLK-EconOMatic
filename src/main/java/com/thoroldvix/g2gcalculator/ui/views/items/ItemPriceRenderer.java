package com.thoroldvix.g2gcalculator.ui.views.items;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ItemPriceRenderer extends HorizontalLayout {


    public ItemPriceRenderer(long value) {
        long gold = value / 10000;
        long silver = (value - gold * 10000) / 100;
        long copper = value - gold * 10000 - silver * 100;


        addDisplayForCurrency("gold", gold);
        addDisplayForCurrency("silver", silver);
        addDisplayForCurrency("copper", copper);


        setAlignItems(Alignment.CENTER);
    }

    private void addDisplayForCurrency(String type, long value) {
        HorizontalLayout layout = new HorizontalLayout();
        Image currencyImage = new Image(String.format("images/%s_coin.png", type), type);
        currencyImage.setWidth("20px");
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(false);
        layout.getThemeList().add("spacing-xs");
        layout.setVisible(value != 0);
        layout.add(new Text(String.valueOf(value)), currencyImage);
        add(layout);
    }
}
