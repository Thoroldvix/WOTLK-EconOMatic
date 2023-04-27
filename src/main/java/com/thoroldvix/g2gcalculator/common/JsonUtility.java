package com.thoroldvix.g2gcalculator.common;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtility {

    public static String getTextValue(JsonNode node, String... keys) {
        return getNode(node, keys).asText();
    }

    public static int getIntValue(JsonNode node, String... keys) {
        return getNode(node, keys).asInt();
    }

    public static long getLongValue(JsonNode node, String... keys) {
        return getNode(node, keys).asLong();
    }

    private JsonNode getNode(JsonNode node, String... keys) {
    for (String key : keys) {
        node = node.get(key);
        if (node == null) {
            throw new IllegalArgumentException("Missing key: " + key);
        }
    }
    return node;
}
}
