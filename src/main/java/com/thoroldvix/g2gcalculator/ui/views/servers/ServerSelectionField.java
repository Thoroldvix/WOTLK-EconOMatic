package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.ui.views.items.ItemGridLayout;
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
                                ItemGridLayout itemGridLayout, ServerView serverView) {
        this.factionSelect = factionSelect;
        this.serverSelect = serverSelect;

        addValueChangeListener(event -> {
            if (event.getValue().faction() != null && event.getValue().name() != null) {
                itemGridLayout.populateGridForServer(event.getValue());
                serverView.setup(event.getValue().getFullServerName());
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
}
