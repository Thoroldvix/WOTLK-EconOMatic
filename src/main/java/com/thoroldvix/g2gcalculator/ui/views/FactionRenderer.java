package com.thoroldvix.g2gcalculator.ui.views;

import com.thoroldvix.g2gcalculator.server.Faction;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class FactionRenderer extends HorizontalLayout {

    private final Faction faction;

    public FactionRenderer(Faction faction) {
        this.faction = faction;
        setAlignItems(Alignment.CENTER);
        setSpacing(false);
        getThemeList().set("spacing-s", true);
        String imagePath = faction.name().equals("HORDE") ? "images/horde.svg"
                : "images/alliance.svg";
        Image image = new Image(imagePath, "faction-icon");
        image.setWidth("20px");
        image.setHeight("20px");
        add(image, new Text(faction.toString()));
    }
}
