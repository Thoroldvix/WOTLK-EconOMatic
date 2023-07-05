package com.thoroldvix.economatic.goldprice.mapper;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceListResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPricePageResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.model.Server;
import com.thoroldvix.economatic.server.model.ServerType;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;


class GoldPriceMapperTest {
     public static final BigDecimal PRICE1_VALUE = new BigDecimal("0.2");
    public static final BigDecimal PRICE2_VALUE = new BigDecimal("0.1");
    public static final String SERVER1_UNIQUE_NAME = "everlook-alliance";
    public static final String SERVER2_UNIQUE_NAME = "gehennas-horde";

    public static final String SERVER1_NAME = "Everlook";
    public static final String SERVER2_NAME = "Gehennas";
    public static final int SERVER1_ID = 41003;
    public static final int SERVER2_ID = 41023;
    private final GoldPriceMapper goldPriceMapper = Mappers.getMapper(GoldPriceMapper.class);
    private final LocalDateTime now = LocalDateTime.now();
    private GoldPrice goldPrice1;
    private GoldPrice goldPrice2;
    private GoldPriceResponse goldPriceResponse1;
    private GoldPriceResponse goldPriceResponse2;

    @BeforeEach
    public void setUp() {
        Server server1 = buildServer(Faction.ALLIANCE, SERVER1_ID,
                ServerType.PVE, Locale.GERMAN, SERVER1_NAME, SERVER1_UNIQUE_NAME);

        Server server2 = buildServer(Faction.HORDE, SERVER2_ID, ServerType.PVP,
                Locale.ENGLISH, SERVER2_NAME, SERVER2_UNIQUE_NAME);


        goldPrice1 = new GoldPrice(1L, PRICE1_VALUE, now, server1);
        goldPrice2 = new GoldPrice(2L, PRICE2_VALUE, now, server2);

        goldPriceResponse1 = new GoldPriceResponse(PRICE1_VALUE, SERVER1_UNIQUE_NAME, now);
        goldPriceResponse2 =  new GoldPriceResponse(PRICE2_VALUE, SERVER2_UNIQUE_NAME, now);
    }

    @Test
    void toGoldPriceList_returnsValidGoldPriceListResponse_whenListOfGoldPriceProvided() {
        GoldPriceListResponse expected = new GoldPriceListResponse(List.of(goldPriceResponse1, goldPriceResponse2));
        GoldPriceListResponse actual = goldPriceMapper.toGoldPriceList(List.of(goldPrice1, goldPrice2));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toList_returnsValidGoldPriceResponseList_whenValidGoldPriceListProvided() {
        List<GoldPriceResponse> response = goldPriceMapper.toList(List.of(goldPrice1, goldPrice2));
        assertThat(response).containsExactly(goldPriceResponse1, goldPriceResponse2);
    }

    @Test
    void toPageResponse_returnsValidGoldPricePageResponse_whenValidGoldPricePageProvided() {
        PageImpl<GoldPrice> page = new PageImpl<>(List.of(goldPrice1, goldPrice2));
        GoldPricePageResponse expected = buildGoldPricePageResponse(goldPriceResponse1, goldPriceResponse2, page);
        GoldPricePageResponse actual = goldPriceMapper.toPageResponse(page);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toResponse_returnsValidGoldPriceResponse_whenValidGoldPriceProvided() {
        GoldPriceResponse actual = goldPriceMapper.toResponse(goldPrice1);
        assertThat(actual).isEqualTo(goldPriceResponse1);
    }

    private GoldPricePageResponse buildGoldPricePageResponse(GoldPriceResponse goldPriceResponse1, GoldPriceResponse goldPriceResponse2, Page<?> page) {
        PaginationInfo paginationInfo = new PaginationInfo(page);
        return new GoldPricePageResponse(paginationInfo, List.of(goldPriceResponse1, goldPriceResponse2));
    }

    private Server buildServer(Faction faction, int id, ServerType type, Locale locale, String name, String uniqueName) {
        return Server.builder()
                .type(type)
                .faction(faction)
                .id(id)
                .region(Region.EU)
                .locale(locale)
                .name(name)
                .uniqueName(uniqueName)
                .build();
    }
}