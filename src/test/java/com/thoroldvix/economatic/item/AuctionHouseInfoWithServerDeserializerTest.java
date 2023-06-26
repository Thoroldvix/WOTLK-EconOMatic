package com.thoroldvix.economatic.item;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionHouseInfoWithServerDeserializerTest {

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

    }

}