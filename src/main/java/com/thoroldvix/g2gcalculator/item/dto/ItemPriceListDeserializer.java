package com.thoroldvix.g2gcalculator.item.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.thoroldvix.g2gcalculator.common.JsonUtility.*;

public class ItemPriceListDeserializer extends StdDeserializer<ItemPriceList> {

    public ItemPriceListDeserializer() {
        this(null);
    }
    public ItemPriceListDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public ItemPriceList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<ItemPrice> items = new ArrayList<>();
        String slug = getTextValue(node, "slug");
        JsonNode resultNode = node.get("data");
        if (resultNode.isArray()) {
            for (JsonNode item : resultNode) {
                ItemPrice itemInfo = getAuctionHouseInfo(item);
                items.add(itemInfo);
            }
        }

        return new ItemPriceList(slug, items);
    }

    private ItemPrice getAuctionHouseInfo(JsonNode item) {
        int itemId = getIntValue(item, "itemId");
        long marketValue = getLongValue(item, "marketValue");
        long minBuyout = getLongValue(item, "minBuyout");
        int numAuctions = getIntValue(item, "numAuctions");
        int quantity = getIntValue(item, "quantity");

        return ItemPrice.builder()
                .itemId(itemId)
                .marketValue(marketValue)
                .minBuyout(minBuyout)
                .numAuctions(numAuctions)
                .quantity(quantity)
                .build();
    }

}
