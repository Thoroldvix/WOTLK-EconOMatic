package com.thoroldvix.pricepal.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.pricepal.auctionhouse.AuctionHouseInfo;
import com.thoroldvix.pricepal.auctionhouse.AuctionHouseInfoDeserializer;
import com.thoroldvix.pricepal.auctionhouse.ItemPrice;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionHouseInfoDeserializerTest {

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
        AuctionHouseInfoDeserializer deserializer = new AuctionHouseInfoDeserializer();
        JsonParser jp = new ObjectMapper().getFactory().createParser(json);
        DeserializationContext ctxt = new ObjectMapper().getDeserializationContext();
        ItemPrice itemInfo1 = ItemPrice.builder()
                .id(43)
                .marketValue(2216718)
                .minBuyout(0)
                .numAuctions(0)
                .quantity(0)
                .build();


        ItemPrice itemInfo2 = ItemPrice.builder()
                .id(53)
                .marketValue(196866)
                .minBuyout(0)
                .numAuctions(0)
                .quantity(0)
                .build();
        List<ItemPrice> itemInfoList = List.of(itemInfo1, itemInfo2);

        AuctionHouseInfo expectedResult = AuctionHouseInfo.builder()
                .slug("everlook-alliance")
                .items(itemInfoList)
                .build();

        AuctionHouseInfo actualResult = deserializer.deserialize(jp, ctxt);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

}