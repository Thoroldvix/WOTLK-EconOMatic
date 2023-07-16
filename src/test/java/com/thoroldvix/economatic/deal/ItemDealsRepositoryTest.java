package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.PostgresqlContainerInitializer;
import com.thoroldvix.economatic.item.Item;
import com.thoroldvix.economatic.item.ItemQuality;
import com.thoroldvix.economatic.item.ItemSlot;
import com.thoroldvix.economatic.item.ItemType;
import com.thoroldvix.economatic.itemprice.ItemPrice;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.Server;
import com.thoroldvix.economatic.server.ServerType;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@ActiveProfiles(profiles = {"integration"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemDealsRepositoryTest implements PostgresqlContainerInitializer {

    private static final String[] ITEM_DEALS_PROJECTION_FIELDS = new String[]{
            "itemId",
            "uniqueServerName",
            "marketValue",
            "minBuyout",
            "dealDiff",
            "discountPercentage",
            "itemName"};

    @Autowired
    private ItemDealsRepository itemDealsRepository;
    private Tuple itemPrice1Tuple;
    private Tuple itemPrice2Tuple;
    private Tuple itemPrice3Tuple;
    private Tuple itemPrice4Tuple;
    private Tuple itemPrice5Tuple;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        Server server1 = new Server(1, "server1", Region.EU, Faction.ALLIANCE, Locale.GERMAN, ServerType.PVE, "server1-alliance");
        Server server2 = new Server(2, "server1", Region.US, Faction.HORDE, Locale.US, ServerType.PVP, "server2-alliance");

        persistAll(List.of(server1, server2));

        Item item1 = buildItem(1, "item1", ItemQuality.RARE);
        Item item2 = buildItem(2, "item2", ItemQuality.UNCOMMON);
        Item item3 = buildItem(3, "item3", ItemQuality.COMMON);
        Item item4 = buildItem(4, "item4", ItemQuality.EPIC);
        Item item5 = buildItem(5, "item5", ItemQuality.LEGENDARY);

        persistAll(List.of(item1, item2, item3, item4, item5));

        ItemPrice itemPrice1 = buildItemPrice(item1, 2, 5, 6, 12, server1);
        ItemPrice itemPrice2 = buildItemPrice(item2, 10, 20, 2, 4, server2);
        ItemPrice itemPrice3 = buildItemPrice(item3, 5, 40, 1, 1, server1);
        ItemPrice itemPrice4 = buildItemPrice(item4, 400, 900, 60, 120, server1);
        ItemPrice itemPrice5 = buildItemPrice(item5, 10000, 300, 5, 20, server1);

        persistAll(List.of(itemPrice1, itemPrice2, itemPrice3, itemPrice4, itemPrice5));

        itemPrice1Tuple = getTupleForItemPrice(itemPrice1, 3, 60);
        itemPrice2Tuple = getTupleForItemPrice(itemPrice2, 10, 50);
        itemPrice3Tuple = getTupleForItemPrice(itemPrice3, 35, 88);
        itemPrice4Tuple = getTupleForItemPrice(itemPrice4, 500, 56);
        itemPrice5Tuple = getTupleForItemPrice(itemPrice5, -9700, -32.3);


    }

    @AfterEach
    void cleanup() {
        entityManager.clear();
    }

    @Test
    void findDealsForServer_returnsCorrectDeals_forServerId() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(2, 1, 0, 5);
        assertThat(dealsForServer)
                .hasSize(1)
                .extracting(ITEM_DEALS_PROJECTION_FIELDS)
                .containsExactly(itemPrice2Tuple);
    }

    @Test
    void findDealsForServer_returnsDealsOrderedByDiscountPercentage() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(1, 1, 0, 5);
        assertThat(dealsForServer)
                .hasSize(3)
                .extracting(ITEM_DEALS_PROJECTION_FIELDS)
                .containsExactly(itemPrice3Tuple, itemPrice1Tuple, itemPrice4Tuple);
    }

    @Test
    void findDealsForServer_returnsCorrectDeals_whenLimitIsSet() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(1, 1, 0, 2);
        assertThat(dealsForServer)
                .hasSize(2)
                .extracting(ITEM_DEALS_PROJECTION_FIELDS)
                .containsExactlyInAnyOrder(itemPrice1Tuple, itemPrice3Tuple);
    }


    @Test
    void findDealsForServer_returnsCorrectDeals_whenFilteredByMinQuality() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(1, 1, 2, 5);
        assertThat(dealsForServer)
                .hasSize(2)
                .extracting(ITEM_DEALS_PROJECTION_FIELDS)
                .containsExactlyInAnyOrder(itemPrice1Tuple, itemPrice4Tuple);
    }

    @Test
    void findDealsForServer_returnsCorrectDeals_whenFilteredByMinQuantity() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(1, 12, 0, 5);
        assertThat(dealsForServer)
                .hasSize(2)
                .extracting(ITEM_DEALS_PROJECTION_FIELDS)
                .containsExactlyInAnyOrder(itemPrice1Tuple, itemPrice4Tuple);
    }

    @Test
    void findDealsForServer_ignoresItemPrice_whenMinBuyoutGreaterThanMarketValue() {
        List<ItemDealProjection> dealsForServer = itemDealsRepository.findDealsForServer(1, 1, 0, 5);
        assertThat(dealsForServer)
                .hasSize(3)
                .extracting(ITEM_DEALS_PROJECTION_FIELDS)
                .doesNotContain(itemPrice5Tuple);
    }

    private ItemPrice buildItemPrice(Item item3, long minBuyout, long marketValue, int numAuctions, int quantity, Server server) {
        return ItemPrice.builder()
                .server(server)
                .item(item3)
                .minBuyout(minBuyout)
                .marketValue(marketValue)
                .numAuctions(numAuctions)
                .quantity(quantity)
                .build();
    }

    private Tuple getTupleForItemPrice(ItemPrice itemPrice, long dealDiff, double discountPercentage) {
        return tuple(itemPrice.getItem().getId(),
                itemPrice.getServer().getUniqueName(),
                itemPrice.getMarketValue(),
                itemPrice.getMinBuyout(),
                dealDiff,
                BigDecimal.valueOf(discountPercentage).setScale(2, RoundingMode.HALF_UP),
                itemPrice.getItem().getUniqueName());
    }

    private Item buildItem(int id, String name, ItemQuality quality) {
        return Item.builder()
                .id(id)
                .name(name)
                .type(ItemType.TRADE_GOODS)
                .slot(ItemSlot.NON_EQUIPABLE)
                .vendorPrice(0)
                .quality(quality)
                .build();
    }

    private <E> void persistAll(List<E> entities) {
        for (E entity : entities) {
            entityManager.persist(entity);
        }
    }
}