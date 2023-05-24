package com.thoroldvix.pricepal.ui.item.component;

import com.thoroldvix.pricepal.item.api.ItemsController;
import com.thoroldvix.pricepal.item.dto.ItemInfo;
import com.thoroldvix.pricepal.ui.item.renderer.ItemNameRenderer;
import com.thoroldvix.pricepal.ui.item.view.ItemView;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.data.domain.PageRequest;

import java.util.Set;

@SpringComponent
@UIScope
public class ItemSearchBar extends ComboBox<ItemInfo> {
    public ItemSearchBar(ItemsController itemsController) {

        addValueChangeListener(event ->
                navigateToItemsLayout(event.getValue()));

        setWidth("20%");
        Set<ItemInfo> items = itemsController.getAllItems(PageRequest.of(0, Integer.MAX_VALUE)).getBody();
        setItems(items);
        setPageSize(5);
        setItemLabelGenerator(ItemInfo::name);
        setRenderer(new ComponentRenderer<>(ItemNameRenderer::new));
        setPlaceholder("Search item...");
        setPrefixComponent(VaadinIcon.SEARCH.create());


    }

    private void navigateToItemsLayout(ItemInfo value) {
        getUI().ifPresent(ui -> ui.navigate(ItemView.class, value.uniqueName()));
    }



}
