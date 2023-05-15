package com.thoroldvix.g2gcalculator.ui.views;

import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
@UIScope
@SpringComponent
public class ServerSelect extends ComboBox<ServerResponse> {

    public ServerSelect(ServerService serverServiceImpl) {
        Set<ServerResponse> servers = new TreeSet<>(Comparator.comparing(ServerResponse::name));
        servers.addAll(serverServiceImpl.getAllServers());
        setItems(servers);
        setWidth("215px");
        addClassName("server-select");
        setAllowCustomValue(false);
        setPlaceholder("Select Server");
        setRequired(true);
        setRequiredIndicatorVisible(true);
        setItemLabelGenerator(ServerResponse::name);
    }

}
