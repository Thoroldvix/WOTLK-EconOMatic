package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@CssImport("./styles/shared-styles.css")
public class ItemNameRenderer extends HorizontalLayout {

    private final ItemInfo itemInfo;

    public ItemNameRenderer(ItemInfo itemInfo) {
        this.itemInfo = itemInfo;

        configureItemIconDisplay();
        configureItemNameDisplay();

        setAlignItems(Alignment.CENTER);
        setWidthFull();
    }

    private void configureItemIconDisplay() {
        Image itemIcon = new Image(itemInfo.icon(), "icon");
        itemIcon.setWidth("40px");
        add(itemIcon);
    }

    private void configureItemNameDisplay() {
        Span name = new Span(itemInfo.name());
        name.getClassNames().add(itemInfo.quality().name().toLowerCase());
        add(name);
    }
}
