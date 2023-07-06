package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.model.Server;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class GoldPriceSetupBaseTest {
    private final LocalDateTime updateDate = LocalDateTime.parse("2023-07-04T16:07:53.195131");
    protected GoldPrice goldPrice1;
    protected GoldPrice goldPrice2;
    protected GoldPrice goldPrice3;
    protected GoldPrice goldPrice4;
    protected GoldPrice goldPrice5;
    protected GoldPrice goldPrice6;
    protected GoldPrice goldPrice7;
    protected GoldPrice goldPrice8;
    protected GoldPrice goldPrice9;
    protected GoldPrice goldPrice10;
    protected GoldPrice goldPrice11;

    private static Server createServer(int id, Region region, Faction faction) {
        return Server.builder()
                .id(id)
                .region(region)
                .faction(faction)
                .build();
    }

    @BeforeEach
    void setup() {
        Server server1 = createServer(41074, Region.EU, Faction.HORDE);
        Server server2 = createServer(46326, Region.EU, Faction.HORDE);
        Server server3 = createServer(41070, Region.EU, Faction.HORDE);
        Server server4 = createServer(41066, Region.EU, Faction.HORDE);
        Server server5 = createServer(41037, Region.EU, Faction.HORDE);
        Server server6 = createServer(46320, Region.US, Faction.HORDE);
        Server server7 = createServer(41310, Region.US, Faction.ALLIANCE);

        goldPrice1 = new GoldPrice(1L, BigDecimal.valueOf(0.000870), updateDate, server1);
        goldPrice2 = new GoldPrice(2L, BigDecimal.valueOf(0.000801), updateDate, server2);
        goldPrice3 = new GoldPrice(3L, BigDecimal.valueOf(0.001205), updateDate, server3);
        goldPrice4 = new GoldPrice(4L, BigDecimal.valueOf(0.000938), updateDate, server4);
        goldPrice5 = new GoldPrice(5L, BigDecimal.valueOf(0.000854), updateDate, server5);
        goldPrice6 = new GoldPrice(6L, BigDecimal.valueOf(0.001148), updateDate, server6);
        goldPrice7 = new GoldPrice(7L, BigDecimal.valueOf(0.001216), updateDate, server7);
        goldPrice8 = new GoldPrice(8L, BigDecimal.valueOf(0.3), LocalDateTime.parse("2023-01-04T16:07:53.195131"), server3);
        goldPrice9 = new GoldPrice(9L, BigDecimal.valueOf(0.4), LocalDateTime.parse("2022-07-04T16:07:53.195131"), server5);
        goldPrice10 = new GoldPrice(10L, BigDecimal.valueOf(0.2), LocalDateTime.parse("2021-07-04T16:07:53.195131"), server5);
        goldPrice11 = new GoldPrice(11L, BigDecimal.valueOf(1), LocalDateTime.parse("2021-07-04T16:07:53.195131"), server2);
    }
}
