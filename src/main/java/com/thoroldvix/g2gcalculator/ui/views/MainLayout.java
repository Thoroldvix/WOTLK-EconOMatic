package com.thoroldvix.g2gcalculator.ui.views;

import com.thoroldvix.g2gcalculator.ui.views.items.AuctionHouseView;
import com.thoroldvix.g2gcalculator.ui.views.items.ServerSelectionField;
import com.thoroldvix.g2gcalculator.ui.views.servers.ServerGridView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.util.Optional;

@Route("wow-classic")
@PageTitle("PricePal")
public class MainLayout extends AppLayout {
    private final Tabs menu;
    private final ServerSelectionField serverSelectionField;

    private HorizontalLayout headerLayout;
    private H1 viewTitle;


    public MainLayout(ServerSelectionField serverSelectionField) {
        this.serverSelectionField = serverSelectionField;

        menu = createMenu();
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());

        addToDrawer(createDrawerContent(menu));
    }


    private Component createHeaderContent() {
        headerLayout = new HorizontalLayout();


        headerLayout.setId("header");
        headerLayout.getThemeList().set("dark", true);
        headerLayout.setWidthFull();
        headerLayout.setSpacing(false);
        headerLayout.getThemeList().set("spacing-s", true);
        headerLayout.setPadding(false);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);


        viewTitle = new H1();
        viewTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        HorizontalLayout titleLayout = new HorizontalLayout(new DrawerToggle(), viewTitle);
        titleLayout.setWidthFull();
        titleLayout.setSpacing(false);
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        headerLayout.add(titleLayout);

        return headerLayout;
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();


        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        // Have a drawer header with an application logo
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        H1 appTitle = new H1("PricePal");
        appTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("line-height", "var(--lumo-size-l)")
                .set("margin", "0 var(--lumo-space-m)");

        logoLayout.add(appTitle);


        layout.add(logoLayout, menu);
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();


        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);


        viewTitle.setText(getCurrentPageTitle());

        if (getContent().getClass().equals(AuctionHouseView.class)) {
            headerLayout.add(serverSelectionField);
        } else {
            headerLayout.remove(serverSelectionField);
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