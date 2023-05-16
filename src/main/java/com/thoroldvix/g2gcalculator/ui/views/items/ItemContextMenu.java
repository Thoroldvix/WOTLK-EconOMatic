package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;

public class ItemContextMenu {
    private final GridContextMenu<ItemInfo> contextMenu;


    public ItemContextMenu(GridContextMenu<ItemInfo> contextMenu) {
        this.contextMenu = contextMenu;
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("VIEW ON WOWHEAD", event -> event.getItem()
                .ifPresent(this::navigateToWowhead));

    }
    private void navigateToWowhead(ItemInfo item) {
        contextMenu.getUI()
                .ifPresent(ui -> ui.getPage().open(item.getWowheadUrl()));
    }
}
