package com.thoroldvix.g2gcalculator.ui.views.servers;

import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.thoroldvix.g2gcalculator.ui.views.FactionRenderer;
import com.thoroldvix.g2gcalculator.ui.views.FactionSelect;
import com.thoroldvix.g2gcalculator.ui.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;


@Route(value = "wow-classic/servers", layout = MainLayout.class)
@PageTitle("G2G Prices")
@SpringComponent
@UIScope
public class ServerGridView extends VerticalLayout {
    private final ServerService serverServiceImpl;

    private final Grid<ServerResponse> grid = new Grid<>(ServerResponse.class);

    private TextField serverNameFilter;
    private FactionSelect factionFilter;

    public ServerGridView(ServerService serverServiceImpl) {
        this.serverServiceImpl = serverServiceImpl;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        updateGrid();
        prepareFilterFields();
        add(getFilterLayout(), getContent());
    }

    private void updateGrid() {
        grid.setItems(serverServiceImpl.getAllServers());
    }

    private Component getFilterLayout() {
        HorizontalLayout filterLayout = new HorizontalLayout(serverNameFilter);
        filterLayout.addClassName("filter-layout");
        filterLayout.setAlignItems(Alignment.CENTER);
        filterLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        filterLayout.add(serverNameFilter, factionFilter);
        return filterLayout;
    }

    private void prepareFilterFields() {
        serverNameFilter = getServerNameFilter();
        factionFilter = getFactionFilter();
    }

    private FactionSelect getFactionFilter() {
        FactionSelect factionSelect = new FactionSelect();
        factionSelect.setHelperText("Select faction");
        factionSelect.addThemeVariants(SelectVariant.LUMO_HELPER_ABOVE_FIELD);
        factionSelect.addValueChangeListener(e -> onFilterChange());
        return factionSelect;
    }

    private TextField getServerNameFilter() {
        TextField filter = new TextField();
        filter.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        filter.setPlaceholder("Search...");
        filter.setHelperText("Search for server");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> onFilterChange());
        return filter;
    }

    private void onFilterChange() {
        GridListDataView<ServerResponse> listDataProvider =  grid.getListDataView();
        listDataProvider.setFilter(serverResponse -> {
                    boolean serverNameMatch = true;
                    boolean factionMatch = true;
                    if (!serverNameFilter.isEmpty()) {
                        serverNameMatch = serverResponse.name().toLowerCase().contains(serverNameFilter.getValue().toLowerCase());
                    }
                    if (factionFilter.getValue() != null) {
                        factionMatch = serverResponse.faction().equals(factionFilter.getValue());
                    }
                    return serverNameMatch && factionMatch;
                }
        );
    }

    private void configureGrid() {
        grid.addClassName("server-grid");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();
        configureColumns();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addItemClickListener(event -> navigateToServer(event.getItem()));
    }

    private void configureColumns() {
        grid.addColumn(ServerResponse::name).setHeader("Name");
        grid.addColumn(new ComponentRenderer<>(server -> new FactionRenderer(server.faction()))).setHeader("Faction");
        grid.addColumn(ServerResponse::region).setHeader("Region");
        grid.addColumn(serverResponse -> serverResponse.price().value()).setHeader("Price (USD)");

        grid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setSortable(true);
        });
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