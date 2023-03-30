package com.thoroldvix.g2gcalculator.g2g;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.thoroldvix.g2gcalculator.price.PriceResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class G2GPriceDeserializer extends StdDeserializer<G2GPriceResponse> {

    public G2GPriceDeserializer() {
        this(null);
    }

    public G2GPriceDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public G2GPriceResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<PriceResponse> prices = new ArrayList<>();
        JsonNode resultsNode = node.get("payload").get("results");
        if (resultsNode.isArray()) {
            for (JsonNode resultNode : resultsNode) {
                PriceResponse price = PriceResponse.builder()
                        .serverName(resultNode.get("title").asText())
                        .currency(resultNode.get("display_currency").asText())
                        .value(resultNode.get("converted_unit_price").decimalValue())
                        .build();
                prices.add(price);
            }
        }
        return new G2GPriceResponse(prices);
    }
}