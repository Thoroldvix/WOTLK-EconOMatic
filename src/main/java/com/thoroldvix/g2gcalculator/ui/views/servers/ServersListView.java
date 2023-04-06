package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.price.PriceService;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route(value = "wow-classic/servers", layout = MainLayout.class)
public class ServersListView extends VerticalLayout {
    private final ServerService serverServiceImpl;

    private final PriceService priceServiceImpl;
    private final Grid<ServerResponse> grid = new Grid<>(ServerResponse.class);


    private final TextField filterText = new TextField();

    public ServersListView(ServerService serverServiceImpl, PriceService priceServiceImpl) {
        this.priceServiceImpl = priceServiceImpl;
        this.serverServiceImpl = serverServiceImpl;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        add(getToolbar(), getContent());
        updateList();
    }

    private void updateList() {
        grid.setItems(serverServiceImpl.getAllServersByName(filterText.getValue()));
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("server-grid");
        grid.setSizeFull();
        configureColumns();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addItemClickListener(event -> {
            navigateToServer(event.getItem());
        });
    }

    private void configureColumns() {
        grid.addColumn(ServerResponse::name).setHeader("Name")
                .setSortable(true);
        grid.addColumn(ServerResponse::faction).setHeader("Faction")
                .setSortable(true);
        grid.addColumn(ServerResponse::region).setHeader("Region")
                .setSortable(true);
        grid.addColumn(serverResponse -> serverResponse.price().value()).setHeader("Price")
                .setSortable(true);
        grid.addColumn(serverResponse -> serverResponse.price().currency()).setHeader("Currency")
                .setSortable(true);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void navigateToServer(ServerResponse server) {
        getUI().ifPresent(ui -> ui.navigate(ServerView.class, server.id()));
    }


    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }
}