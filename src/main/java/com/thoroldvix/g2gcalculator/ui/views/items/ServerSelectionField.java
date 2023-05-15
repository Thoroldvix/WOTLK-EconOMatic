package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.ui.views.FactionSelect;
import com.thoroldvix.g2gcalculator.ui.views.ServerSelect;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class ServerSelectionField extends CustomField<ServerResponse> {
    private final FactionSelect factionSelect = new FactionSelect();
    private final ServerSelect serverSelect;

    public ServerSelectionField(ServerSelect serverSelect, ItemGridLayout itemGrid) {
        this.serverSelect = serverSelect;
        addValueChangeListener(event -> {
            if (event.getValue().faction() != null && event.getValue().name() != null) {
                itemGrid.populateGrid(getServerName(event.getValue().name(),  event.getValue().faction()));
            }

        });
        configureFactionSelect();
        HorizontalLayout layout = new HorizontalLayout(serverSelect, factionSelect);
        add(layout);
    }

    private void configureFactionSelect() {
        factionSelect.setEmptySelectionAllowed(false);
        factionSelect.setRequiredIndicatorVisible(true);
    }

    @Override
    protected ServerResponse generateModelValue() {
        return ServerResponse.builder()
                .name(serverSelect.getValue().name())
                .faction(factionSelect.getValue())
                .build();
    }
    @Override
    protected void setPresentationValue(ServerResponse newPresentationValue) {
        factionSelect.setValue(newPresentationValue.faction());
        serverSelect.setValue(newPresentationValue);
    }
    private String getServerName(String serverName, Faction faction) {
        return serverName.replaceAll(" ", "-").toLowerCase() + "-" + faction.toString().toLowerCase();
    }
}
