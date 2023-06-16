package com.thoroldvix.pricepal.itemprice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AuctionHouseInfoDeserializer extends StdDeserializer<AuctionHouseInfo> {

    public AuctionHouseInfoDeserializer() {
        this(null);
    }

    public AuctionHouseInfoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AuctionHouseInfo deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<ItemPriceResponse> items = new ArrayList<>();
        String slug = node.get("slug").asText();
        JsonNode resultNode = node.get("data");
        if (resultNode.isArray()) {
            for (JsonNode item : resultNode) {
                ItemPriceResponse itemInfo = getItemPrice(item);
                items.add(itemInfo);
            }
        }
        return new AuctionHouseInfo(slug, items);
    }

    private ItemPriceResponse getItemPrice(JsonNode item) {
        int itemId = item.get("itemId").asInt();
        long historicalValue = item.get("historicalValue").asLong();
        long marketValue = item.get("marketValue").asLong();
        long minBuyout = item.get("minBuyout").asLong();
        int numAuctions = item.get("numAuctions").asInt();
        int quantity = item.get("quantity").asInt();

        return ItemPriceResponse.builder()
                .itemId(itemId)
                .historicalValue(historicalValue)
                .marketValue(marketValue)
                .minBuyout(minBuyout)
                .numAuctions(numAuctions)
                .quantity(quantity)
                .build();

    }
}

