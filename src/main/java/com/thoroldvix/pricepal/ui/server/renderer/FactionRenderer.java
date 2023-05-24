package com.thoroldvix.pricepal.ui.server.renderer;

import com.thoroldvix.pricepal.server.entity.Faction;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class FactionRenderer extends HorizontalLayout {
    private final Image hordeIcon = new Image("images/horde.svg", "horde-icon");
    private final Image allianceIcon = new Image("images/alliance.svg", "alliance-icon");

    public FactionRenderer(Faction faction) {
        configureIcons();
        setAlignItems(Alignment.CENTER);
        setSpacing(false);
        getThemeList().set("spacing-s", true);
        Image factionIcon = faction.name().equals("HORDE") ? hordeIcon
                : allianceIcon;
        add(new Text(faction.toString()), factionIcon);
    }
    private void configureIcons() {
        allianceIcon.setWidth("20px");
        allianceIcon.setHeight("20px");

        hordeIcon.setWidth("20px");
        hordeIcon.setHeight("20px");
    }
}
