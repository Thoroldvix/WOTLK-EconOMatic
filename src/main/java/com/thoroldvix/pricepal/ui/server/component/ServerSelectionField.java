package com.thoroldvix.pricepal.ui.server.component;

import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.ui.item.component.ItemGrid;
import com.thoroldvix.pricepal.ui.server.view.ServerView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class ServerSelectionField extends CustomField<ServerResponse> {
    private final FactionSelect factionSelect;
    private final ServerSelect serverSelect;

    public ServerSelectionField(FactionSelect factionSelect, ServerSelect serverSelect,
                                ItemGrid itemGrid, ServerView serverView) {
        this.factionSelect = factionSelect;
        this.serverSelect = serverSelect;

        addValueChangeListener(event -> {
            if (event.getValue().faction() != null && event.getValue().name() != null) {
                itemGrid.populateGridForServer(event.getValue());
                serverView.setup(formatServerName(event.getValue()));
            }
        });

        configureFactionSelect();
        HorizontalLayout layout = new HorizontalLayout(serverSelect, factionSelect);
        layout.setSpacing(false);
        layout.getThemeList().add("spacing-xs");
        add(layout);
    }

    private void configureFactionSelect() {
        factionSelect.setEmptySelectionAllowed(false);
        factionSelect.setRequiredIndicatorVisible(true);
    }

    @Override
    protected ServerResponse generateModelValue() {
        if (serverSelect.getValue() != null && factionSelect.getValue() != null) {
            return ServerResponse.builder()
                    .name(serverSelect.getValue().name())
                    .faction(factionSelect.getValue())
                    .build();
        }
        return null;
    }

    @Override
    protected void setPresentationValue(ServerResponse newPresentationValue) {
        factionSelect.setValue(newPresentationValue.faction());
        serverSelect.setValue(newPresentationValue);
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        ServerResponse selectedServer = (ServerResponse) VaadinSession.getCurrent().getAttribute("selectedServer");

        setValue(selectedServer);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        ServerResponse selectedServer = getValue();

        VaadinSession.getCurrent().setAttribute("selectedServer", selectedServer);
    }

    private String formatServerName(ServerResponse server) {
        return server.name().replaceAll("'", "").replaceAll(" ", "-")
                .toLowerCase() + "-" + server.faction().name().toLowerCase();
    }
}