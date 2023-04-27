//package com.thoroldvix.g2gcalculator.ui.views.items;
//
//import com.thoroldvix.g2gcalculator.item.ItemService;
//import com.thoroldvix.g2gcalculator.item.dto.ItemStats;
//import com.thoroldvix.g2gcalculator.server.ServerResponse;
//import com.vaadin.flow.component.Key;
//import com.vaadin.flow.component.dependency.CssImport;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.orderedlayout.BoxSizing;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.spring.annotation.SpringComponent;
//import com.vaadin.flow.spring.annotation.UIScope;
//import org.springframework.util.StringUtils;
//
//import java.util.Optional;
//
//@SpringComponent
//@UIScope
//@CssImport("./styles/shared-styles.css")
//public class ItemSearchView extends HorizontalLayout {
//
//    private final ServerSelectionView serverSelectionView;
//
//    private final ItemService itemServiceImpl;
//
//    private final ItemOverview itemOverview;
//    private TextField searchField;
//
//    public ItemSearchView(ServerSelectionView serverSelectionView, ItemService itemServiceImpl, ItemOverview itemOverview) {
//        this.serverSelectionView = serverSelectionView;
//        this.itemServiceImpl = itemServiceImpl;
//        this.itemOverview = itemOverview;
//        setClassName("item-search-view");
//        createSearchField();
//
//
//        setJustifyContentMode(JustifyContentMode.CENTER);
//        setBoxSizing(BoxSizing.CONTENT_BOX);
//        setDefaultVerticalComponentAlignment(Alignment.BASELINE);
//        setPadding(true);
//        setWidthFull();
//
//        add(serverSelectionView);
//    }
//
//    private void createSearchField() {
//        searchField = new TextField();
//
//        searchField.setClassName("search-field");
//        searchField.getElement().setAttribute("aria-label", "search");
//        searchField.setPlaceholder("Search items...");
//        searchField.setClearButtonVisible(true);
//        searchField.setAutofocus(true);
//        searchField.setWidthFull();
//        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
//
//
//        searchField.addKeyUpListener(Key.ENTER, keyDownEvent -> {
//            if (StringUtils.hasText(searchField.getValue())) {
//                searchItem(searchField.getValue());
//            } else {
//                searchField.setInvalid(true);
//                searchField.setErrorMessage("Please enter item name");
//            }
//        });
//
//    }
//
//    private void searchItem(String itemName) {
//        Optional<ServerResponse> server = getServer();
//        if (server.isEmpty()) {
//            searchField.setInvalid(true);
//            searchField.setErrorMessage("Please select server");
//            return;
//        }
//        String serverName = getServerName(server.get());
//        ItemStats itemStats;
//        try {
//            itemStats = itemServiceImpl.getItemByName(serverName, itemName);
//            itemOverview.updateItemInfo(itemStats);
//        } catch (Exception e) {
//            searchField.setInvalid(true);
//            searchField.setErrorMessage("Item with name " + itemName + " not found");
//        }
//    }
//
//
//    private Optional<ServerResponse> getServer() {
//        return Optional.ofNullable(serverSelectionView.getServerSelect().getValue());
//    }
//
//    private String getServerName(ServerResponse server) {
//        return server.name().replaceAll(" ", "-").toLowerCase() + "-" + server.faction().toString().toLowerCase();
//    }
//}