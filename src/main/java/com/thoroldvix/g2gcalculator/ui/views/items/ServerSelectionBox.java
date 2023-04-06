package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;

import java.util.HashSet;

@Getter
@SpringComponent
@UIScope
public class ServerSelectionBox extends VerticalLayout {
    private final ServerService serverServiceImpl;
    private ComboBox<ServerResponse> serverSelect;
    private RadioButtonGroup<String> factionSelect;
    private Faction faction = Faction.ALLIANCE;

    public ServerSelectionBox(ServerService serverServiceImpl) {
        this.serverServiceImpl = serverServiceImpl;
        setClassName("server-selection-box");

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        configureServerSelect();
        configureFactionSelect();

        add(serverSelect, factionSelect);
    }


    private void configureFactionSelect() {
        factionSelect = new RadioButtonGroup<>();
        setClassName("faction-select");
        factionSelect.setRequired(true);
        factionSelect.setRequiredIndicatorVisible(true);
        factionSelect.setErrorMessage("Please select a faction");
        factionSelect.setItems("Alliance", "Horde");
        factionSelect.setValue("Alliance");
        factionSelect.addValueChangeListener(event -> {
            if (event.getValue().equals("Horde")) {
                faction = Faction.HORDE;
            }
        });
    }

    private void configureServerSelect() {
        serverSelect = new ComboBox<>();
        serverSelect.setPlaceholder("Select server");
        serverSelect.setAllowCustomValue(false);
        serverSelect.setRequired(true);
        serverSelect.setRequiredIndicatorVisible(true);
        serverSelect.setErrorMessage("Please select a server");
        serverSelect.setItems(new HashSet<>(this.serverServiceImpl.getAllServers()));
        serverSelect.setItemLabelGenerator(ServerResponse::name);
    }


}