package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.server.ServerController;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@UIScope
@SpringComponent
public class ServerSelect extends ComboBox<ServerResponse> {

    public ServerSelect(ServerController serverController) {
        Set<ServerResponse> servers = new TreeSet<>(Comparator.comparing(ServerResponse::name));
        servers.addAll(Objects.requireNonNull(serverController.getAllServers().getBody()));
        setItems(servers);
        addClassName("server-select");
        setAllowCustomValue(false);
        setPlaceholder("Select Server");
        setRequired(true);
        setRequiredIndicatorVisible(true);
        setItemLabelGenerator(ServerResponse::name);
    }

}
