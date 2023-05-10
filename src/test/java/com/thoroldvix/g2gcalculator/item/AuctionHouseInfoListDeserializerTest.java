package com.thoroldvix.g2gcalculator.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.g2gcalculator.item.dto.AuctionHouseInfo;
import com.thoroldvix.g2gcalculator.item.dto.AuctionHouseInfoList;
import com.thoroldvix.g2gcalculator.item.dto.AuctionHouseInfoListDeserializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionHouseInfoListDeserializerTest {

    @Test
    void testDeserialize() throws IOException {
        String json = """
                   {
                   "slug": "everlook-alliance",
                       "data": [
                           {
                               "itemId": 43,
                               "previous": {
                                   "marketValue": null,
                                   "historicalValue": null,
                                   "minBuyout": null,
                                   "numAuctions": null,
                                   "quantity": null
                               },
                               "historicalValue": 2216718,
                               "marketValue": 2216718,
                               "minBuyout": 0,
                               "numAuctions": 0,
                               "quantity": 0
                           },
                           {
                               "itemId": 53,
                               "previous": {
                                   "marketValue": 193451,
                                   "minBuyout": 223016,
                                   "numAuctions": 1,
                                   "quantity": 1,
                                   "historicalValue": 223015
                               },
                               "historicalValue": 223016,
                               "marketValue": 196866,
                               "minBuyout": 0,
                               "numAuctions": 0,
                               "quantity": 0
                               }
                               ]
                    }
                """;
        AuctionHouseInfoListDeserializer deserializer = new AuctionHouseInfoListDeserializer();
        JsonParser jp = new ObjectMapper().getFactory().createParser(json);
        DeserializationContext ctxt = new ObjectMapper().getDeserializationContext();
        AuctionHouseInfo itemInfo1 = AuctionHouseInfo.builder()
                .itemId(43)
                .marketValue(2216718)
                .minBuyout(0)
                .numAuctions(0)
                .quantity(0)
                .build();


        AuctionHouseInfo itemInfo2 = AuctionHouseInfo.builder()
                .itemId(53)
                .marketValue(196866)
                .minBuyout(0)
                .numAuctions(0)
                .quantity(0)
                .build();
        List<AuctionHouseInfo> itemInfoList = List.of(itemInfo1, itemInfo2);

        AuctionHouseInfoList expectedResult = AuctionHouseInfoList.builder()
                .slug("everlook-alliance")
                .items(itemInfoList)
                .build();

        AuctionHouseInfoList actualResult = deserializer.deserialize(jp, ctxt);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

}