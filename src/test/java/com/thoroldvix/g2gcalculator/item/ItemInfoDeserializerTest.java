package com.thoroldvix.g2gcalculator.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfoDeserializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;


class ItemInfoDeserializerTest {

    @Test
    public void testDeserialize() throws IOException {
        String json = """
                {
                    "server": "everlook-alliance",
                    "itemId": 13444,
                    "name": "Major Mana Potion",
                    "icon": "https://wow.zamimg.com/images/wow/icons/large/inv_potion_76.jpg",
                    "tags": [
                            "Common",
                            "Consumable"
                        ],
                    "stats": {
                        "lastUpdated": "2023-04-01T13:27:24.000Z",
                        "current": {
                            "historicalValue": 6661,
                            "marketValue": 7054,
                            "minBuyout": 2894,
                            "numAuctions": 19,
                            "quantity": 54
                        },
                        "previous": {
                            "marketValue": 7099,
                            "numAuctions": 18,
                            "quantity": 57,
                            "minBuyout": 2895,
                            "historicalValue": 6537
                        }
                    }
                }""";


        JsonParser jp = new ObjectMapper().getFactory().createParser(json);
        DeserializationContext ctxt = new ObjectMapper().getDeserializationContext();
        LocalDateTime lastUpdated = Instant.parse("2023-04-01T13:27:24.000Z")
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();

        ItemInfo expectedResult = ItemInfo.builder()
                .server("everlook-alliance")
                .numAuctions(19)
                .itemId(13444)
                .name("Major Mana Potion")
                .marketValue(7054L)
                .minBuyout(2894L)
                .quantity(54)
                .lastUpdated(lastUpdated)
                .icon("https://wow.zamimg.com/images/wow/icons/large/inv_potion_76.jpg")
                .quality(ItemQuality.COMMON)
                .type(ItemType.CONSUMABLE)
                .build();

        ItemInfo actualResult = new ItemInfoDeserializer().deserialize(jp, ctxt);


        assertThat(actualResult).isEqualTo(expectedResult);
    }
}