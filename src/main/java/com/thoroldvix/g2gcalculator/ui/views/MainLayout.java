package com.thoroldvix.g2gcalculator.ui.views;

import com.thoroldvix.g2gcalculator.ui.views.items.AuctionHouseView;
import com.thoroldvix.g2gcalculator.ui.views.items.ItemsLayout;
import com.thoroldvix.g2gcalculator.ui.views.items.ServerSelectionField;
import com.thoroldvix.g2gcalculator.ui.views.servers.ServerGridView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.awt.*;
import java.util.Optional;

@Route("wow-classic")
@PageTitle("PricePal")
public class MainLayout extends AppLayout {
    private final Tabs menu;
    private final ServerSelectionField serverSelectionField;

    private HorizontalLayout headerLayout = new HorizontalLayout();

    private TextField itemSearchBar = new TextField();


    public MainLayout(ServerSelectionField serverSelectionField) {
        this.serverSelectionField = serverSelectionField;

        menu = createMenu();
        setPrimarySection(Section.NAVBAR);
        configureItemSearchBar();
        addToNavbar(true, createLogoLayout(), createHeaderContent());

        addToDrawer(createDrawerContent(menu));
    }

    private void configureItemSearchBar() {
        itemSearchBar.setWidthFull();
        itemSearchBar.setPlaceholder("Search items...");
        itemSearchBar.setPrefixComponent(VaadinIcon.SEARCH.create());
    }

    private Component createHeaderContent() {
        configureHeaderLayout();
        headerLayout.add(itemSearchBar, serverSelectionField);

        return headerLayout;
    }

    private HorizontalLayout createLogoLayout() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setSpacing(false);
        logoLayout.setMargin(false);
        logoLayout.setPadding(false);
        logoLayout.getThemeList().set("padding-s", true);
        logoLayout.getThemeList().set("spacing-s", true);

        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        H1 appTitle = new H1("PricePal");
        appTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("font-weight", "bold")
                .set("line-height", "var(--lumo-size-l)");


        logoLayout.add(new DrawerToggle(), appTitle);


        return logoLayout;
    }

    private void configureHeaderLayout() {
        headerLayout.setId("header");
        headerLayout.setWidth("50%");
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setSpacing(false);
        headerLayout.setMargin(false);
        headerLayout.getThemeList().set("spacing-s", true);
        headerLayout.getThemeList().set("margin-s", true);
        headerLayout.getStyle().set("margin", "auto");
        headerLayout.setPadding(false);
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        layout.add(menu);
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);

        if (getContent().getClass().equals(ServerGridView.class)) {
            headerLayout.remove(serverSelectionField);
        } else {
            headerLayout.add(serverSelectionField);
        }
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren()
                .filter(tab -> ComponentUtil.getData(tab, Class.class)
                        .equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
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
        return new Tab[]{createTab("G2G Prices", ServerGridView.class),
                createTab("Auction House", AuctionHouseView.class),
                createTab("Items", ItemsLayout.class)
        };
    }

    private static Tab createTab(String text,
                                 Class<? extends Component> navigationTarget) {
        Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }
}