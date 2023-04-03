package com.thoroldvix.g2gcalculator.ui.views;

import com.thoroldvix.g2gcalculator.ui.views.list.ServersListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("wow-classic")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createNavbar();
    }


    private void createDrawer() {
        RouterLink listView = new RouterLink("Servers", ServersListView.class);
        listView.setHighlightCondition(HighlightConditions.sameLocation());
        addToDrawer(new VerticalLayout(listView));
    }

    private void createNavbar() {
        H1 logo = new H1("PricePal");
         logo.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "absolute");

        setPrimarySection(Section.NAVBAR);

        Tabs tabs = getTabs();
        FlexLayout centeredLayout = new FlexLayout();
        centeredLayout.setSizeFull();
        centeredLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centeredLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        centeredLayout.add(tabs);

        addToNavbar(logo, centeredLayout);

    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();

        tabs.add(new Tab(new RouterLink("Servers", ServersListView.class)));
        tabs.add(new Tab("Items"));
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
        return tabs;
    }

}