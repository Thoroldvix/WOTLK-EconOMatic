package com.thoroldvix.pricepal.item.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.thoroldvix.pricepal.common.StringEnumConverter;

import java.io.IOException;

public class ItemDeserializer extends StdDeserializer<Item> {

    public ItemDeserializer() {
        this(null);
    }
    public ItemDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public Item deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        int id = node.get("id").asInt();
        String name = node.get("name").asText();
        String icon = node.get("icon").asText();
        ItemType type = StringEnumConverter.fromString(node.get("class").asText(), ItemType.class);
        ItemQuality quality = StringEnumConverter.fromString(node.get("quality").asText(), ItemQuality.class);
        String itemLink = node.get("itemLink").asText();
        int sellPrice = node.get("sellPrice").asInt();
        int itemLevel = node.get("itemLevel").asInt();
        int requiredLevel = node.get("requiredLevel").asInt();
        String uniqueName = node.get("uniqueName").asText();


        return Item.builder()
                .id(id)
                .itemLink(itemLink)
                .uniqueName(uniqueName)
                .name(name)
                .icon(icon)
                .type(type)
                .quality(quality)
                .sellPrice(sellPrice)
                .itemLevel(itemLevel)
                .requiredLevel(requiredLevel)
                .build();
    }
}
