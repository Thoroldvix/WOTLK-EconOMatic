package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemsController;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class ItemSearchBar extends ComboBox<ItemInfo> {
    public ItemSearchBar(ItemsController itemsController) {

        addValueChangeListener(event ->
                navigateToItemsLayout(event.getValue()));
        setWidth("20%");
        setItems(itemsController.getAllItems());
        setItemLabelGenerator(ItemInfo::name);
        setRenderer(new ComponentRenderer<>(ItemNameRenderer::new));
        setPlaceholder("Search items...");
        setPrefixComponent(VaadinIcon.SEARCH.create());


    }

    private void navigateToItemsLayout(ItemInfo value) {
        getUI().ifPresent(ui -> ui.navigate(ItemsLayout.class, value.getFormattedItemName()));
    }



}
