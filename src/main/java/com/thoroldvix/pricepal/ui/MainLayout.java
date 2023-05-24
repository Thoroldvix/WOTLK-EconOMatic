package com.thoroldvix.pricepal.ui;

import com.thoroldvix.pricepal.ui.item.component.ItemSearchBar;
import com.thoroldvix.pricepal.ui.item.view.AuctionHouseView;
import com.thoroldvix.pricepal.ui.server.component.ServerSelectionField;
import com.thoroldvix.pricepal.ui.server.view.G2GPricesView;
import com.thoroldvix.pricepal.ui.server.view.ServerView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;

import java.util.Optional;


@PageTitle("PricePal")
public class MainLayout extends AppLayout {
    private final Tabs menu;
    private final ServerSelectionField serverSelectionField;

    private final HorizontalLayout headerLayout = new HorizontalLayout();
    private final HorizontalLayout logoLayout = new HorizontalLayout();

    private final ItemSearchBar itemSearchBar;


    public MainLayout(ServerSelectionField serverSelectionField, ItemSearchBar itemSearchBar) {
        this.serverSelectionField = serverSelectionField;
        this.itemSearchBar = itemSearchBar;
        menu = createMenu();
        setPrimarySection(Section.NAVBAR);
        configureHeaderLayout();
        configureLogoLayout();
        addToNavbar(true, logoLayout, headerLayout);
        addToDrawer(createDrawerContent(menu));
    }


    private void configureLogoLayout() {
        H1 appTitle = new H1("PricePal");
        appTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("font-weight", "bold")
                .set("line-height", "var(--lumo-size-l)");

        logoLayout.setSpacing(false);
        logoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        logoLayout.add(new DrawerToggle(), appTitle);
    }

    private void configureHeaderLayout() {
        headerLayout.setWidthFull();
        headerLayout.setSpacing(false);
        headerLayout.setPadding(false);
        headerLayout.setMargin(false);

        headerLayout.getThemeList().add("spacing-xs");
        headerLayout.setAlignItems(FlexComponent.Alignment.START);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        headerLayout.add(itemSearchBar, serverSelectionField);
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        layout.add(menu);
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);


        headerLayout.add(serverSelectionField);
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren()
                .filter(tab -> ComponentUtil.getData(tab, Class.class)
                        .equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }


    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        return tabs;
    }

    private Tab[] createMenuItems() {
        return new Tab[]{createTab("Auction House", AuctionHouseView.class, VaadinIcon.COIN_PILES.create()),
                createTab("G2G Prices", G2GPricesView.class, VaadinIcon.BOOK_DOLLAR.create()),
                createTab("Server", ServerView.class, VaadinIcon.GLOBE_WIRE.create())

        };
    }

    private static Tab createTab(String text,
                                 Class<? extends Component> navigationTarget, Icon icon) {
        Tab tab = new Tab();
        tab.add(icon, new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }
}