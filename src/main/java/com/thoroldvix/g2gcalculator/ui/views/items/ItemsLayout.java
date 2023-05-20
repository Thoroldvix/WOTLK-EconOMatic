package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.thoroldvix.g2gcalculator.ui.views.servers.ServerSelectionField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@Route(value = "wow-classic/items", layout = MainLayout.class)
@PageTitle("Items")
public class ItemsLayout extends VerticalLayout implements HasUrlParameter<String> {

    private final ServerSelectionField serverSelectionField;



    public ItemsLayout(ServerSelectionField serverSelectionField) {
        this.serverSelectionField = serverSelectionField;
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

    }
}
