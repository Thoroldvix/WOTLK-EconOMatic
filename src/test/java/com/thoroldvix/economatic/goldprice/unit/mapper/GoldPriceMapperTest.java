package com.thoroldvix.economatic.goldprice.unit.mapper;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceListResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPricePageResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.mapper.GoldPriceMapper;
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

    private final GoldPriceMapper goldPriceMapper = Mappers.getMapper(GoldPriceMapper.class);
    private final LocalDateTime now = LocalDateTime.now();
    private GoldPrice goldPrice1;
    private GoldPrice goldPrice2;
    private GoldPriceResponse goldPriceResponse1;
    private GoldPriceResponse goldPriceResponse2;

    @BeforeEach
    public void setUp() {
        String server1UniqueName = "server1-alliance";
        String server2UniqueName = "server2-horde";
        BigDecimal price1 = new BigDecimal("0.2");
        BigDecimal price2 = new BigDecimal("0.1");

        Server server1 = buildServer(Faction.ALLIANCE, 1,
                ServerType.PVE, Locale.GERMAN, "server1", server1UniqueName);

        Server server2 = buildServer(Faction.HORDE, 2, ServerType.PVP,
                Locale.ENGLISH, "server2", server2UniqueName);


        goldPrice1 = new GoldPrice(1L, price1, now, server1);
        goldPrice2 = new GoldPrice(2L, price2, now, server2);

        goldPriceResponse1 = new GoldPriceResponse(price1, server1UniqueName, now);
        goldPriceResponse2 = new GoldPriceResponse(price2, server2UniqueName, now);
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