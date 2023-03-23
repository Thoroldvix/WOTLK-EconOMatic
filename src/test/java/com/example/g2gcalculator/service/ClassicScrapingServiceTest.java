package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.*;
import com.example.g2gcalculator.repository.ClassicRealmRepository;
import com.example.g2gcalculator.service.impl.ClassicScrapingService;
import com.example.g2gcalculator.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static com.example.g2gcalculator.util.TestUtil.createRealm;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClassicScrapingServiceTest {
    @Mock
    private ClassicRealmRepository classicRealmRepository;

    @InjectMocks
    private  ClassicScrapingService classicScrapingService;

    @Test
    void getPriceDataForRealm_shouldReturnValidPrice() {
        Realm realm = createRealm();
        realm.setName("Everlook");
        realm.setRegion(Region.DE);

        Price priceDataForRealm = classicScrapingService.fetchRealmPrice(realm);
        assertThat(priceDataForRealm.getPrice()).isNotNull();
        assertThat(priceDataForRealm.getUpdatedAt()).isNotNull();
    }

}