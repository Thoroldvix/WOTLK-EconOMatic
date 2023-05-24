package com.thoroldvix.pricepal.ui.item.view;

import com.thoroldvix.pricepal.ui.MainLayout;
import com.thoroldvix.pricepal.ui.server.component.ServerSelectionField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@Route(value = "wow-classic/items", layout = MainLayout.class)
@PageTitle("Items")
public class ItemView extends VerticalLayout implements HasUrlParameter<String> {

    private final ServerSelectionField serverSelectionField;



    public ItemView(ServerSelectionField serverSelectionField) {
        this.serverSelectionField = serverSelectionField;
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

    }
}
