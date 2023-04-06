package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemService;
import com.thoroldvix.g2gcalculator.item.ItemStats;
import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.util.StringUtils;

@SpringComponent
@UIScope
public class ItemSearchView extends HorizontalLayout {

    private final ServerSelectionBox serverSelectionBox;

    private final ItemService itemServiceImpl;

    private final ItemInfoView itemInfo;
    private TextField searchField;

    public ItemSearchView(ServerSelectionBox serverSelectionBox, ItemService itemServiceImpl, ItemInfoView itemInfo) {
        this.serverSelectionBox = serverSelectionBox;
        this.itemServiceImpl = itemServiceImpl;
        this.itemInfo = itemInfo;
        setClassName("item-search-view");
        createSearchField();

        setAlignItems(Alignment.BASELINE);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSpacing(true);

        add(searchField, serverSelectionBox);
    }

    private void createSearchField() {
        searchField = new TextField();

        searchField.setClassName("search-field");
        searchField.getElement().setAttribute("aria-label", "search");
        searchField.setPlaceholder("Search items...");
        searchField.setClearButtonVisible(true);
        searchField.setAutofocus(true);
        searchField.setMaxWidth("100%");
        searchField.setWidth("auto");

        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());


        searchField.addKeyUpListener(Key.ENTER, keyDownEvent -> {
            if (StringUtils.hasText(searchField.getValue())) {
                searchItem(searchField.getValue());
            }
        });

    }

    private void searchItem(String itemName) {
        ServerResponse server = getServer();
        String serverName = getServerName(server);
        ItemStats itemStats;
        try {
            itemStats = itemServiceImpl.getItemByName(serverName, itemName);
            itemInfo.updateItemInfo(itemStats);
        } catch (Exception e) {
            searchField.setInvalid(true);
            searchField.setErrorMessage("Item with name " + itemName + " not found");
        }
    }


    private ServerResponse getServer() {
        ServerResponse serverSelect = serverSelectionBox.getServerSelect().getValue();
        Faction faction = serverSelectionBox.getFaction();
        return ServerResponse.builder()
                .id(serverSelect.id())
                .name(serverSelect.name())
                .faction(faction)
                .build();
    }

    private String getServerName(ServerResponse server) {
        return server.name().replaceAll(" ", "-").toLowerCase() + "-" + server.faction().toString().toLowerCase();
    }
}