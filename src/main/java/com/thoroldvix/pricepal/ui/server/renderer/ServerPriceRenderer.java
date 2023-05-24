package com.thoroldvix.pricepal.ui.server.renderer;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ServerPriceRenderer extends HorizontalLayout {
    public ServerPriceRenderer(BigDecimal price) {
        double formattedPrice = price.multiply(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.HALF_UP).doubleValue();
        setSpacing(false);
        getThemeList().add("spacing-s");
        add(getPriceLayout(formattedPrice), new Text("/"), getGoldLayout());
    }

    private HorizontalLayout getGoldLayout() {
        Image goldImage = new Image("images/gold_coin.png", "Gold");
        HorizontalLayout goldLayout = new HorizontalLayout(new Text("1000"), goldImage);
        goldImage.setWidth("12px");
        goldImage.setHeight("12px");
        goldLayout.setAlignItems(Alignment.CENTER);
        goldLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        goldLayout.setSpacing(false);
        goldLayout.getThemeList().add("spacing-xs");
        return goldLayout;
    }

    private HorizontalLayout getPriceLayout(double formattedPrice) {
        HorizontalLayout priceLayout = new HorizontalLayout();
        priceLayout.setAlignItems(Alignment.CENTER);
        priceLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        Span priceSpan = new Span(String.format("%.2f%s", formattedPrice, formattedPrice < 1 ? "¢" : "$"));

        priceLayout.add(priceSpan);
        return priceLayout;
    }
}
