package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.model.Faction;
import com.thoroldvix.g2gcalculator.model.Price;
import com.thoroldvix.g2gcalculator.model.Realm;
import com.thoroldvix.g2gcalculator.model.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ClassicScrapingServiceTest {

    @Autowired
    private  ScrapingService classicScrapingService;


    @Test
    void getPriceDataForRealm_shouldReturnValidPrice() {
        Realm realm = Realm.builder()
                .id(1)
                .name("Everlook")
                .faction(Faction.ALLIANCE)
                .region(Region.DE)
                .build();



        Price priceDataForRealm = classicScrapingService.fetchRealmPrice(realm);
        assertThat(priceDataForRealm.getValue()).isNotNull();
        assertThat(priceDataForRealm.getUpdatedAt()).isNotNull();
    }

}