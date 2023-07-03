package com.thoroldvix.economatic.goldprice.mapper;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceListResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.model.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class GoldPriceMapperTest {

    public static final BigDecimal PRICE1_VALUE = new BigDecimal("0.2");
    public static final BigDecimal PRICE2_VALUE = new BigDecimal("0.1");
    public static final String SERVER1_NAME = "everlook-alliance";
    public static final String SERVER2_NAME = "gehennas-horde";
    private GoldPriceMapper goldPriceMapper = Mappers.getMapper(GoldPriceMapper.class);

    private Server server1;
    private Server server2;
    private LocalDateTime now;
    private GoldPrice goldPrice1;
    private GoldPrice goldPrice2;

    @BeforeEach
    public void setUp() {

        server1 = buildServer(Faction.ALLIANCE, 41003, Region.EU,
                Locale.GERMAN, "Everlook", SERVER1_NAME);

        server2 = buildServer(Faction.HORDE, 41023, Region.EU,
                Locale.ENGLISH, "Gehennas", SERVER2_NAME);

        now = LocalDateTime.now();

        goldPrice1 = buildGoldPrice(1L, PRICE1_VALUE, server1, now);
        goldPrice2 = buildGoldPrice(2L, PRICE2_VALUE, server2, now);
    }

    @Test
    void toGoldPriceList_returnsValidGoldPriceListResponse_whenListOfGoldPriceProvided() {
        GoldPriceResponse goldPriceResponse1 = buildGoldPriceResponse(PRICE1_VALUE, SERVER1_NAME);
        GoldPriceResponse goldPriceResponse2 = buildGoldPriceResponse(PRICE2_VALUE, SERVER2_NAME);

        GoldPriceListResponse expectedResponse = new GoldPriceListResponse(List.of(goldPriceResponse1, goldPriceResponse2));
        GoldPriceListResponse actualResponse = goldPriceMapper.toGoldPriceList(List.of(goldPrice1, goldPrice2));

        assertThat(expectedResponse).isEqualTo(actualResponse);
    }
    @Test
    void toList_returnsValidGoldPriceResponseList_whenValidGoldPriceListProvided() {

    }

    @Test
    void toPageResponse_returnsValidGoldPricePageResponse_whenValidGoldPricePageProvided() {

    }

    private GoldPriceResponse buildGoldPriceResponse(BigDecimal val, String server) {
        return GoldPriceResponse.builder()
                .price(val)
                .server(server)
                .updatedAt(now)
                .build();
    }

    private GoldPrice buildGoldPrice(Long id, BigDecimal value, Server server, LocalDateTime updatedAt) {
        return GoldPrice.builder()
                .id(id)
                .value(value)
                .server(server)
                .updatedAt(updatedAt)
                .build();
    }

    private Server buildServer(Faction faction, int id, Region region, Locale locale, String name, String uniqueName) {
        return Server.builder()
                .faction(faction)
                .id(id)
                .region(region)
                .locale(locale)
                .name(name)
                .uniqueName(uniqueName)
                .build();
    }
}