package com.thoroldvix.pricepal.ui.server.component;

import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.ui.server.renderer.FactionRenderer;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class FactionSelect extends Select<Faction> {
    public FactionSelect() {
        addClassName("faction-select");
        setPlaceholder("Select Faction");
        setEmptySelectionAllowed(true);
        setEmptySelectionCaption("Any");
        setRenderer(new ComponentRenderer<>(FactionRenderer::new));
        setItems(Faction.values());
    }




}
