package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemQuality;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.function.Predicate;

@CssImport("./styles/shared-styles.css")
public class ItemFilteringLayout extends HorizontalLayout {
    private TextField itemNameFilter;
    private MultiSelectComboBox<ItemQuality> itemQualityFilter;
    private final ItemGridView itemGrid;



    public ItemFilteringLayout(ItemGridView itemGrid) {
        this.itemGrid = itemGrid;
        setVisible(false);
        prepareFilterFields();
        add(itemNameFilter, itemQualityFilter);
    }

    private void prepareFilterFields() {
        itemNameFilter = getItemNameFilter();
        itemQualityFilter = getItemQualityFilter();
    }

    private MultiSelectComboBox<ItemQuality> getItemQualityFilter() {
        MultiSelectComboBox<ItemQuality> itemQualityFilter = new MultiSelectComboBox<>();
        itemQualityFilter.setPlaceholder("Select item's quality...");
        itemQualityFilter.setItems(ItemQuality.values());
        itemQualityFilter.setItemLabelGenerator(ItemQuality::toString);
        itemQualityFilter.addValueChangeListener(event -> itemGrid.onFilterChange(getFilter()));
        return itemQualityFilter;
    }

    private TextField getItemNameFilter() {
        TextField itemNameFilter = new TextField();
        itemNameFilter.setPlaceholder("Filter by item name...");
        itemNameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        itemNameFilter.setClearButtonVisible(true);
        itemNameFilter.addValueChangeListener(event -> itemGrid.onFilterChange(getFilter()));
        return itemNameFilter;
    }

    private Predicate<ItemInfo> getFilter() {
        return itemInfo -> {
            boolean itemNameMatch = true;
            boolean itemQualityMatch = true;
            if (!itemNameFilter.isEmpty()) {
                itemNameMatch = itemInfo.name().toLowerCase().contains(itemNameFilter.getValue().toLowerCase());
            }
            if (!itemQualityFilter.getValue().isEmpty()) {
                itemQualityMatch = itemQualityFilter.getValue().contains(itemInfo.quality());
            }
            return itemNameMatch && itemQualityMatch;
        };
    }
}
