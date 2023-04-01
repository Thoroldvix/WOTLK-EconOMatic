package com.thoroldvix.g2gcalculator.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;


class ItemDeserializerTest {

    @Test
    public void testDeserialize() throws IOException {
        String json = """
                {
                    "server": "everlook-alliance",
                    "itemId": 13444,
                    "name": "Major Mana Potion",
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
        ItemStats result = new ItemDeserializer().deserialize(jp, ctxt);
        LocalDateTime lastUpdated = Instant.parse("2023-04-01T13:27:24.000Z")
                                           .atZone(ZoneOffset.UTC)
                                           .toLocalDateTime();

        assertThat(result.server()).isEqualTo("everlook-alliance");
        assertThat(result.itemId()).isEqualTo(13444);
        assertThat(result.name()).isEqualTo("Major Mana Potion");
        assertThat(result.marketValue()).isEqualTo(7054L);
        assertThat(result.minBuyout()).isEqualTo(2894L);
        assertThat(result.quantity()).isEqualTo(54);
        assertThat(result.lastUpdated()).isEqualTo(lastUpdated);

    }


}