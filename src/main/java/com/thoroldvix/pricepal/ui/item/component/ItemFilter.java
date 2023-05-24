package com.thoroldvix.pricepal.ui.item.component;

import com.thoroldvix.pricepal.item.dto.FullItemInfo;
import com.thoroldvix.pricepal.item.entity.ItemQuality;
import com.thoroldvix.pricepal.item.entity.ItemType;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.function.Predicate;


public class ItemFilter extends HorizontalLayout {
    private TextField itemNameFilter;
    private MultiSelectComboBox<ItemQuality> itemQualityFilter;

    private MultiSelectComboBox<ItemType> itemTypeFilter;


    private final ItemGrid itemGrid;


    public ItemFilter(ItemGrid itemGrid) {
        this.itemGrid = itemGrid;

        prepareFilterFields();
        add(itemNameFilter, itemQualityFilter, itemTypeFilter);
    }

    private void prepareFilterFields() {
        itemNameFilter = getItemNameFilter();
        itemQualityFilter = getItemQualityFilter();
        itemTypeFilter = getItemTypeFilter();
    }

    private MultiSelectComboBox<ItemQuality> getItemQualityFilter() {
        MultiSelectComboBox<ItemQuality> itemQualityFilter = new MultiSelectComboBox<>();
        itemQualityFilter.addThemeVariants(MultiSelectComboBoxVariant.LUMO_HELPER_ABOVE_FIELD);
        itemQualityFilter.setHelperText("Quality");
        itemQualityFilter.setPlaceholder("Any");

        itemQualityFilter.setItems(ItemQuality.values());
        itemQualityFilter.setRenderer(new ComponentRenderer<Component, ItemQuality>(this::getItemQualityName));
        itemQualityFilter.setClearButtonVisible(true);
        itemQualityFilter.addValueChangeListener(event -> itemGrid.onFilterChange(getFilter()));
        return itemQualityFilter;
    }

    private Span getItemQualityName(ItemQuality itemQuality) {
        Span itemQualityName = new Span(itemQuality.toString());
        itemQualityName.setClassName(itemQuality.name().toLowerCase());
        return itemQualityName;
    }


    private MultiSelectComboBox<ItemType> getItemTypeFilter() {
        MultiSelectComboBox<ItemType> itemTypeFilter = new MultiSelectComboBox<>();
        itemTypeFilter.setHelperText("Type");
        itemTypeFilter.addThemeVariants(MultiSelectComboBoxVariant.LUMO_HELPER_ABOVE_FIELD);
        itemTypeFilter.setPlaceholder("Any");
        itemTypeFilter.setWidthFull();
        itemTypeFilter.setItems(ItemType.values());
        itemTypeFilter.setClearButtonVisible(true);
        itemTypeFilter.setItemLabelGenerator(ItemType::toString);
        itemTypeFilter.addValueChangeListener(event -> itemGrid.onFilterChange(getFilter()));
        return itemTypeFilter;
    }

    private TextField getItemNameFilter() {
        TextField itemNameFilter = new TextField();
        itemNameFilter.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        itemNameFilter.setPlaceholder("Search...");

        itemNameFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        itemNameFilter.setHelperText("Name");
        itemNameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        itemNameFilter.setClearButtonVisible(true);
        itemNameFilter.addValueChangeListener(event -> itemGrid.onFilterChange(getFilter()));
        return itemNameFilter;
    }

    private Predicate<FullItemInfo> getFilter() {
        return item -> {
            boolean itemNameMatch = true;
            boolean itemQualityMatch = true;
            boolean itemTypeMatch = true;

            if (!itemNameFilter.isEmpty()) {
                itemNameMatch = item.itemInfo().name().toLowerCase().contains(itemNameFilter.getValue().toLowerCase());
            }
            if (!itemQualityFilter.getValue().isEmpty()) {
                itemQualityMatch = itemQualityFilter.getValue().contains(item.itemInfo().quality());
            }
            if (!itemTypeFilter.getValue().isEmpty()) {
                itemTypeMatch = itemTypeFilter.getValue().contains(item.itemInfo().type());
            }
            return itemNameMatch && itemQualityMatch && itemTypeMatch;
        };
    }
}
