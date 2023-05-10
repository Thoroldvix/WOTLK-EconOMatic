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

public class AuctionHouseInfoListDeserializer extends StdDeserializer<AuctionHouseInfoList> {

    public AuctionHouseInfoListDeserializer() {
        this(null);
    }
    public AuctionHouseInfoListDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public AuctionHouseInfoList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<AuctionHouseInfo> items = new ArrayList<>();
        String slug = getTextValue(node, "slug");
        JsonNode resultNode = node.get("data");
        if (resultNode.isArray()) {
            for (JsonNode item : resultNode) {
                AuctionHouseInfo itemInfo = getAuctionHouseInfo(item);
                items.add(itemInfo);
            }
        }

        return new AuctionHouseInfoList(slug, items);
    }

    private  AuctionHouseInfo getAuctionHouseInfo(JsonNode item) {
        int itemId = getIntValue(item, "itemId");
        long marketValue = getLongValue(item, "marketValue");
        long minBuyout = getLongValue(item, "minBuyout");
        int numAuctions = getIntValue(item, "numAuctions");
        int quantity = getIntValue(item, "quantity");

        return AuctionHouseInfo.builder()
                .itemId(itemId)
                .marketValue(marketValue)
                .minBuyout(minBuyout)
                .numAuctions(numAuctions)
                .quantity(quantity)
                .build();
    }

}
