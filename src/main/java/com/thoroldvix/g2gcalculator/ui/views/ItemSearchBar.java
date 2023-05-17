package com.thoroldvix.g2gcalculator.ui.views;

import com.thoroldvix.g2gcalculator.item.ItemService;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.thoroldvix.g2gcalculator.ui.views.items.ItemNameRenderer;
import com.thoroldvix.g2gcalculator.ui.views.items.ItemsLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class ItemSearchBar extends ComboBox<ItemInfo> {
    public ItemSearchBar(ItemService itemServiceImpl) {

        addValueChangeListener(event ->
                navigateToItemsLayout(event.getValue()));

        setItems(itemServiceImpl.getAllItemsInfo());
        setItemLabelGenerator(ItemInfo::name);
        setRenderer(new ComponentRenderer<>(ItemNameRenderer::new));
        setPlaceholder("Search items...");
        setPrefixComponent(VaadinIcon.SEARCH.create());

        setWidthFull();
    }

    private void navigateToItemsLayout(ItemInfo value) {
        getUI().ifPresent(ui -> ui.navigate(ItemsLayout.class, value.getFormatterItemName()));
    }



}
