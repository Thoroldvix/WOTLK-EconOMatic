package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

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
        itemIcon.setHeight("40px");
        add(itemIcon);
    }

    private void configureItemNameDisplay() {
        VerticalLayout itemNameLayout = new VerticalLayout();
        itemNameLayout.setAlignItems(Alignment.START);
        itemNameLayout.setWidthFull();
        itemNameLayout.setPadding(false);
        itemNameLayout.setSpacing(false);
        itemNameLayout.setMargin(false);

        Span name = new Span(itemInfo.name());
        Span type = new Span(itemInfo.type().toString());

        name.getClassNames().add(itemInfo.quality().name().toLowerCase());
        type.getStyle().set("font-size", "--lumo-font-size-xs");
        type.getStyle().set("color", "--lumo-secondary-text-color");


        itemNameLayout.add(name, type);
        add(itemNameLayout);
    }
}
