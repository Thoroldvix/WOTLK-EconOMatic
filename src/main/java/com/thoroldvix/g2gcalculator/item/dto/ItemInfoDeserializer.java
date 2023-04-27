package com.thoroldvix.g2gcalculator.item.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.thoroldvix.g2gcalculator.common.StringEnumConverter;
import com.thoroldvix.g2gcalculator.item.ItemQuality;
import com.thoroldvix.g2gcalculator.item.ItemType;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.thoroldvix.g2gcalculator.common.JsonUtility.*;

public class ItemInfoDeserializer extends StdDeserializer<ItemInfo> {
    public ItemInfoDeserializer() {
        this(null);
    }

    public ItemInfoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ItemInfo deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        return getItemInfo(node);
    }

    private ItemInfo getItemInfo(JsonNode node) {
    String server = getTextValue(node, "server");
    int itemId = getIntValue(node, "itemId");
    String name = getTextValue(node, "name");
    String icon = getTextValue(node, "icon");
    long marketValue = getLongValue(node, "stats", "current", "marketValue");
    long minBuyout = getLongValue(node, "stats", "current", "minBuyout");
    int quantity = getIntValue(node, "stats", "current", "quantity");
    int numAuctions = getIntValue(node, "stats", "current", "numAuctions");
    LocalDateTime lastUpdated = getLocalDateTimeValue(node, "stats", "lastUpdated");
    ItemType itemType = getItemType(node);
    ItemQuality itemQuality = getItemQuality(node);

    return ItemInfo.builder()
            .server(server)
            .itemId(itemId)
            .name(name)
            .icon(icon)
            .marketValue(marketValue)
            .minBuyout(minBuyout)
            .quantity(quantity)
            .numAuctions(numAuctions)
            .lastUpdated(lastUpdated)
            .type(itemType)
            .quality(itemQuality)
            .build();
}



private LocalDateTime getLocalDateTimeValue(JsonNode node, String... keys) {
    String value = getTextValue(node, keys);
    return Instant.parse(value)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
}

private ItemQuality getItemQuality(JsonNode node) {
        String rarity = node.get("tags").get(0).asText();
        StringEnumConverter<ItemQuality> rarityConverter = new StringEnumConverter<>(ItemQuality.class);
        return rarityConverter.fromString(rarity);
    }

    private ItemType getItemType(JsonNode node) {
        String type = node.get("tags").get(1).asText();
        StringEnumConverter<ItemType> typeConverter = new StringEnumConverter<>(ItemType.class);
        return typeConverter.fromString(type);
    }

}