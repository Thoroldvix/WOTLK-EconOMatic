package com.thoroldvix.g2gcalculator.ui.item.component;

import com.thoroldvix.g2gcalculator.item.api.ItemsController;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.thoroldvix.g2gcalculator.ui.item.renderer.ItemNameRenderer;
import com.thoroldvix.g2gcalculator.ui.item.view.ItemView;
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
        setPlaceholder("Search item...");
        setPrefixComponent(VaadinIcon.SEARCH.create());


    }

    private void navigateToItemsLayout(ItemInfo value) {
        getUI().ifPresent(ui -> ui.navigate(ItemView.class, value.getFormattedItemName()));
    }



}
