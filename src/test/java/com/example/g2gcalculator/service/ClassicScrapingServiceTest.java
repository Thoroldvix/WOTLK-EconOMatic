package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.model.Region;
import com.example.g2gcalculator.model.RegionName;
import com.example.g2gcalculator.repository.ClassicRealmRepository;
import com.example.g2gcalculator.service.impl.ClassicScrapingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClassicScrapingServiceTest {
    @Mock
    private ClassicRealmRepository classicRealmRepository;

    @InjectMocks
    private  ClassicScrapingService classicScrapingService;

    @Test
    void getPriceDataForRealm_shouldWork() {
        Realm realm = Realm.builder()
                .name("Mograine")
                .region(Region.builder().name(RegionName.EU)
                        .g2gId(UUID.fromString("ac3f85c1-7562-437e-b125-e89576b9a38e"))
                        .build())
                .faction(Faction.HORDE)
                .build();

        PriceResponse priceDataForRealm = classicScrapingService.fetchRealmPrice(realm);
        assertThat(priceDataForRealm.value()).isNotNull();
    }

}