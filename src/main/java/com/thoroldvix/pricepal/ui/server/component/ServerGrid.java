package com.thoroldvix.pricepal.ui.server.component;

import com.thoroldvix.pricepal.server.api.ServerController;
import com.thoroldvix.pricepal.server.api.ServerPriceController;
import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.ui.server.renderer.FactionRenderer;
import com.thoroldvix.pricepal.ui.server.renderer.ServerPriceRenderer;
import com.thoroldvix.pricepal.ui.server.view.ServerView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.data.domain.Pageable;

import java.util.Objects;


@SpringComponent
@UIScope
public class ServerGrid extends VerticalLayout {
    private final ServerController serverController;
    private final ServerPriceController serverPriceController;

    private final ServerSelectionField serverSelectionField;

    private final Grid<ServerResponse> grid = new Grid<>();

    private TextField serverNameFilter;
    private FactionSelect factionFilter;

    public ServerGrid(ServerController serverController, ServerPriceController serverPriceController, ServerSelectionField serverSelectionField) {
        this.serverController = serverController;
        this.serverPriceController = serverPriceController;

        this.serverSelectionField = serverSelectionField;
        setSizeFull();
        configureGrid();
        updateGrid();
        setMargin(false);
        prepareFilterFields();
        add(getFilterLayout(), getContent());
    }

    private void updateGrid() {
        grid.setItems(serverController.getAllServers().getBody());
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
        filter.setHelperText("Search server");
        filter.setPrefixComponent(VaadinIcon.SEARCH.create());
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> onFilterChange());
        return filter;
    }

    private void onFilterChange() {
        GridListDataView<ServerResponse> listDataProvider = grid.getListDataView();
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
        grid.addColumn(server -> server.region().name()).setHeader("Region");
        grid.addColumn(new ComponentRenderer<>(server -> new ServerPriceRenderer(getRecentServerPrice(server))))
                .setHeader("Price");
        grid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setSortable(true);
        });
    }

    private ServerPriceResponse getRecentServerPrice(ServerResponse server) {
        return Objects.requireNonNull(serverPriceController.getPricesForServer(server.uniqueName(),  Pageable.unpaged())
                        .getBody())
                .get(0);
    }

    private void navigateToServer(ServerResponse server) {
        serverSelectionField.setValue(server);
        getUI().flatMap(ui ->
                        ui.navigate(ServerView.class))
                .ifPresent(serverView -> serverView.setup(formatServerName(server)));
    }


    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private String formatServerName(ServerResponse server) {
        return server.name().replaceAll("'", "").replaceAll(" ", "-")
                       .toLowerCase() + "-" + server.faction().name().toLowerCase();
    }
}