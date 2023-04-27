package com.thoroldvix.g2gcalculator.item.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.thoroldvix.g2gcalculator.common.JsonUtility.*;

public class ItemInfoListDeserializer extends StdDeserializer<ItemInfoList> {

    public ItemInfoListDeserializer() {
        this(null);
    }
    public ItemInfoListDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public ItemInfoList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<ItemInfo> itemStats = new ArrayList<>();
        String slug = getTextValue(node, "slug");
        JsonNode resultNode = node.get("data");
        if (resultNode.isArray()) {
            for (JsonNode item : resultNode) {
                ItemInfo itemInfo = getItemInfo(item);
                itemStats.add(itemInfo);
            }
        }

        return new ItemInfoList(slug, itemStats);
    }

    private  ItemInfo getItemInfo(JsonNode item) {
        int itemId = getIntValue(item, "itemId");
        long marketValue = getLongValue(item, "marketValue");
        long minBuyout = getLongValue(item, "minBuyout");
        int numAuctions = getIntValue(item, "numAuctions");
        int quantity = getIntValue(item, "quantity");

        return ItemInfo.builder()
                .itemId(itemId)
                .marketValue(marketValue)
                .minBuyout(minBuyout)
                .numAuctions(numAuctions)
                .quantity(quantity)
                .build();
    }

}
