package com.thoroldvix.pricepal.goldprice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoldPriceDeserializer extends StdDeserializer<List<GoldPriceResponse>> {

    public GoldPriceDeserializer() {
        this(null);
    }

    public GoldPriceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public List<GoldPriceResponse> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<GoldPriceResponse> prices = new ArrayList<>();
        JsonNode resultsNode = node.get("payload").get("results");
        if (resultsNode.isArray()) {
            for (JsonNode resultNode : resultsNode) {
                GoldPriceResponse serverPrice = getServerPrice(resultNode);
                prices.add(serverPrice);
            }
        }
        return prices;
    }

    private GoldPriceResponse getServerPrice(JsonNode resultNode) {
        String serverName = formatServerName(resultNode.get("title").asText());
        BigDecimal price = resultNode.get("converted_unit_price").decimalValue();

        return GoldPriceResponse.builder()
                .serverName(serverName)
                .price(price)
                .build();
    }

    private String formatServerName(String serverName) {
        Pattern pattern = Pattern.compile("^(\\w+(?:\\s\\w+)?)\\s.*-\\s(\\w+)$");
        Matcher matcher = pattern.matcher(serverName.replaceAll("'", ""));

        if (matcher.find()) {
            String server = matcher.group(1).toLowerCase().replaceAll("\\s+", "-");
            String faction = matcher.group(2).toLowerCase();

            return server + "-" + faction;
        }
        return serverName;
    }
}