package com.thoroldvix.g2gcalculator.ui.views;

import com.thoroldvix.g2gcalculator.ui.views.items.ItemsView;
import com.thoroldvix.g2gcalculator.ui.views.servers.ServersListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.*;

@Route("wow-classic")
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private Tabs tabs;

    public MainLayout() {
        createNavbar();
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
        tabs = new Tabs();

        Tab servers = new Tab(new RouterLink("Servers", ServersListView.class));
        Tab items = new Tab(new RouterLink("Items", ItemsView.class));
        Tab prices = new Tab("Prices");

        tabs.add(servers, items, prices);
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
        return tabs;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String url = event.getLocation().getPath();

        for (Component tab : tabs.getChildren().toList()) {
            RouterLink link = (RouterLink) tab.getChildren().findFirst().orElseThrow(
                    NotFoundException::new
            );
            if (link.getHref().equals(url)) {
                tabs.setSelectedTab((Tab) tab);
                break;
            }
        }
    }
}