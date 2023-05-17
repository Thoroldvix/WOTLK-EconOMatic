package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.ui.views.FactionSelect;
import com.thoroldvix.g2gcalculator.ui.views.ServerSelect;
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


    public ServerSelectionField(FactionSelect factionSelect, ServerSelect serverSelect, ItemGridLayout itemGridLayout) {
        this.factionSelect = factionSelect;
        this.serverSelect = serverSelect;

        addValueChangeListener(event -> {
            if (event.getValue().faction() != null && event.getValue().name() != null) {
                itemGridLayout.populateGridForServer(event.getValue());
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
