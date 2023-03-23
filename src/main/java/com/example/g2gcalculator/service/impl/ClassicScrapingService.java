package com.example.g2gcalculator.service.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.service.ScrapingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClassicScrapingService implements ScrapingService {
    private final String URL = "https://g2g.com/categories/wow-classic-gold?sort=lowest_price";

    @Override
    public Price fetchRealmPrice(Realm realm) {
        Element realmDiv = findRealmDiv(realm)
                .orElseThrow(() -> new NotFoundException("Didn't find realm with name: "
                                                         + realm.getName()
                                                         + " and faction: " + realm.getFaction().name() + " on g2g.com"));
        log.debug("Found div for realm: " + realm.getName());
        BigDecimal price = extractPrice(realmDiv);
        log.debug("Extracted price: " + price);

        return Price.builder()
                .price(price)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Optional<Element> findRealmDiv(Realm realm) {
        configureSelenide();

        open(URL + "&region_id=" + realm.getRegion().getG2gId());

        $(By.cssSelector("div.row.q-col-gutter-sm-md.q-px-sm-md")).shouldHave(Condition.text(getFullRealmName(realm)));

        String html = getWebDriver().getPageSource();
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByClass("col-xs-12 col-sm-6 col-md-3");

        return elements.stream()
                .filter(e -> e.text().contains(getFullRealmName(realm)))
                .findFirst();
    }

    private BigDecimal extractPrice(Element element) {
        log.debug("Extracting price from div: " + element);
        Element priceSpan = element
                .getElementsByClass("row items-baseline q-gutter-xs text-body1")
                .select("span")
                .get(1);

        return new BigDecimal(priceSpan.text().trim());
    }


    private String getFullRealmName(Realm realm) {
        return String.format("%s [%s] - %s", realm.getName(),
                realm.getRegion(), realm.getFaction().toString());
    }

    private void configureSelenide() {
        Configuration.driverManagerEnabled = false;
        Configuration.browser = "chrome";
        Configuration.timeout = 5000;
        Configuration.remote = "http://localhost:4444/wd/hub";
    }
}