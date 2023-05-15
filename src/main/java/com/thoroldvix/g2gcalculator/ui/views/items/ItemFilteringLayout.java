package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemQuality;
import com.thoroldvix.g2gcalculator.item.ItemType;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.function.Predicate;

@CssImport("./styles/shared-styles.css")
public class ItemFilteringLayout extends HorizontalLayout {
    private TextField itemNameFilter;
    private MultiSelectComboBox<ItemQuality> itemQualityFilter;

    private MultiSelectComboBox<ItemType> itemTypeFilter;

    private PriceRangeField priceRangeField = new PriceRangeField();
    private final ItemGridLayout itemGrid;


    public ItemFilteringLayout(ItemGridLayout itemGrid) {
        this.itemGrid = itemGrid;
        setVisible(false);
        setAlignItems(Alignment.CENTER);
        setVerticalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        setWidthFull();
        prepareFilterFields();
        add(itemNameFilter, itemQualityFilter, itemTypeFilter, priceRangeField);
    }

    private void prepareFilterFields() {
        itemNameFilter = getItemNameFilter();
        itemQualityFilter = getItemQualityFilter();
        itemTypeFilter = getItemTypeFilter();
    }

    private MultiSelectComboBox<ItemQuality> getItemQualityFilter() {
        MultiSelectComboBox<ItemQuality> itemQualityFilter = new MultiSelectComboBox<>();
        itemQualityFilter.addThemeVariants(MultiSelectComboBoxVariant.LUMO_HELPER_ABOVE_FIELD);
        itemQualityFilter.setHelperText("Select Quality");
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
        itemTypeFilter.setHelperText("Select Type");
        itemTypeFilter.addThemeVariants(MultiSelectComboBoxVariant.LUMO_HELPER_ABOVE_FIELD);
        itemTypeFilter.setPlaceholder("Any");
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
        itemNameFilter.setHelperText("Search for item");
        itemNameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        itemNameFilter.setClearButtonVisible(true);
        itemNameFilter.addValueChangeListener(event -> itemGrid.onFilterChange(getFilter()));
        return itemNameFilter;
    }

    private Predicate<ItemInfo> getFilter() {
        return itemInfo -> {
            boolean itemNameMatch = true;
            boolean itemQualityMatch = true;
            boolean itemTypeMatch = true;
            if (!itemNameFilter.isEmpty()) {
                itemNameMatch = itemInfo.name().toLowerCase().contains(itemNameFilter.getValue().toLowerCase());
            }
            if (!itemQualityFilter.getValue().isEmpty()) {
                itemQualityMatch = itemQualityFilter.getValue().contains(itemInfo.quality());
            }
            if (!itemTypeFilter.getValue().isEmpty()) {
                itemTypeMatch = itemTypeFilter.getValue().contains(itemInfo.type());
            }
            return itemNameMatch && itemQualityMatch && itemTypeMatch;
        };
    }
}
