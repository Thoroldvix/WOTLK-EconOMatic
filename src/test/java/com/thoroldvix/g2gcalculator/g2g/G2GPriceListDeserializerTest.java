package com.thoroldvix.g2gcalculator.g2g;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.g2gcalculator.price.PriceResponse;
import com.thoroldvix.g2gcalculator.price.g2g.G2GPriceListDeserializer;
import com.thoroldvix.g2gcalculator.price.g2g.G2GPriceListResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class G2GPriceListDeserializerTest {

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
        G2GPriceListResponse result = new G2GPriceListDeserializer().deserialize(jp, ctxt);
        List<PriceResponse> prices = result.prices();

        assertThat(prices).hasSize(2);
        assertThat(prices.get(0).serverName()).isEqualTo("Giantstalker [EU] - Horde");
        assertThat(prices.get(0).currency()).isEqualTo("USD");
        assertThat(prices.get(0).value()).isEqualTo(BigDecimal.valueOf(0.000624));
        assertThat(prices.get(1).serverName()).isEqualTo("Nethergarde Keep [EU] - Horde");
        assertThat(prices.get(1).currency()).isEqualTo("USD");
        assertThat(prices.get(1).value()).isEqualTo(BigDecimal.valueOf(0.000745));


    }
}