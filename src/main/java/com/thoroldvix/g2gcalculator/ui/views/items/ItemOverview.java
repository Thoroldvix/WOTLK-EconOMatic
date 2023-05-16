package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@Route(value = "wow-classic/items", layout = MainLayout.class)
@PageTitle("Item Overview")
public class ItemOverview extends VerticalLayout implements HasUrlParameter<String> {

    private final ServerSelectionField serverSelectionField;

    private  ItemPriceHistoryLayout itemPriceHistoryLayout;

    public ItemOverview(ServerSelectionField serverSelectionField) {
        this.serverSelectionField = serverSelectionField;
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

    }
}
