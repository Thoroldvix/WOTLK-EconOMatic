package com.thoroldvix.g2gcalculator.ui.views;

import com.thoroldvix.g2gcalculator.server.Faction;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class FactionSelect extends Select<Faction> {

    public FactionSelect() {
        addClassName("faction-select");
        setPlaceholder("Select Faction");
        setRenderer(new ComponentRenderer<>(FactionRenderer::new));
        setRequiredIndicatorVisible(true);
        setItems(Faction.values());
    }
}
