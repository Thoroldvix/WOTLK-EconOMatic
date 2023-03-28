package com.thoroldvix.g2gcalculator.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.thoroldvix.g2gcalculator.error.NotFoundException;
import com.thoroldvix.g2gcalculator.model.Price;
import com.thoroldvix.g2gcalculator.model.Realm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Service
@RequiredArgsConstructor
public class ClassicScrapingService implements ScrapingService {

    private static final String URL = "https://g2g.com/categories/wow-classic-gold?sort=lowest_price";

    @PostConstruct
    private void init() {
        Configuration.driverManagerEnabled = false;
        Configuration.browser = "chrome";
        Configuration.timeout = 6000;
        Configuration.remote = "http://localhost:4444/wd/hub";
    }

    @Override
    public Price fetchRealmPrice(Realm realm) {
        Optional<Element> realmDiv = findRealmDiv(realm);
        if (realmDiv.isEmpty()) {
            throw new NotFoundException("Didn't find realm with name: " + realm.getName()
                                        + " and faction: " + realm.getFaction().name() + " on g2g.com");
        }
        BigDecimal price = extractPrice(realmDiv.get());

        return Price.builder()
                .value(price)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Optional<Element> findRealmDiv(Realm realm) {
        String g2gRegionId = realm.getRegion().getG2gId();
        open(String.format("%s&region_id=%s", URL, g2gRegionId));

        String divText = toDivText(realm);
        $(By.cssSelector("div.row.q-col-gutter-sm-md.q-px-sm-md"))
                .shouldHave(Condition.text(divText));

        WebDriver driver = getWebDriver();
        String html = driver.getPageSource();
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByClass("col-xs-12 col-sm-6 col-md-3");

        driver.close();
        return elements.stream()
                .filter(e -> e.text().contains(divText))
                .findFirst();
    }

    private BigDecimal extractPrice(Element element) {
        Element priceSpan = element
                .getElementsByClass("row items-baseline q-gutter-xs text-body1")
                .select("span")
                .get(1);

        return new BigDecimal(priceSpan.text().trim());
    }

    private String toDivText(Realm realm) {
        return String.format("%s [%s] - %s", realm.getName(),
                realm.getRegion(), realm.getFaction().toString());
    }

}