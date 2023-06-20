package com.thoroldvix.pricepal.goldprice.server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.goldprice.GoldPriceDeserializer;
import com.thoroldvix.pricepal.goldprice.GoldPriceResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class G2GGoldPriceListDeserializerTest {

    @Test
    void testDeserialize() throws IOException {
        String json = """
                {
                "code": 2000,
                "messages": [],
                "payload": {
                "results": [
                {
                "converted_unit_price": 0.000624,
                "display_currency": "USD",
                "title": "Giantstalker [EU] - Horde"
                },
                {
                "converted_unit_price": 0.000745,
                "display_currency": "USD",
                "title": "Nethergarde Keep [EU] - Horde"
                }]
                }}""";


        JsonParser jp = new ObjectMapper().getFactory().createParser(json);
        DeserializationContext ctxt = new ObjectMapper().getDeserializationContext();
        List<GoldPriceResponse> prices = new GoldPriceDeserializer().deserialize(jp, ctxt);

        assertThat(prices).hasSize(2);
        assertThat(prices.get(0).server()).isEqualTo("giantstalker-horde");
        assertThat(prices.get(0).price()).isEqualTo(BigDecimal.valueOf(0.000624));
        assertThat(prices.get(1).server()).isEqualTo("nethergarde-keep-horde");
        assertThat(prices.get(1).price()).isEqualTo(BigDecimal.valueOf(0.000745));
    }
}