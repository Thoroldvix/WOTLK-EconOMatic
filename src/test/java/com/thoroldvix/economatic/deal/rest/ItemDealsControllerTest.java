package com.thoroldvix.economatic.deal.rest;

import com.thoroldvix.economatic.deal.service.ItemDealsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class ItemDealsControllerTest {

    public static final String SERVER_IDENTIFIER = "everlook-alliance";
    @Mock
    private ItemDealsService itemDealsService;
    @InjectMocks
    private ItemDealsController itemDealsController;

    private MockMvc mockMvc;

    @Test
    void testGetDealsForServer_returnsCorrectsItemDealsResponse_whenServerIdentifierIsValid() {
        String serverIdentifier = SERVER_IDENTIFIER;
        String expectedJsonResponse = """ 
                {
                "server": "everlook-alliance",
                "deals": [
                {
                "itemId": 22449,
                "marketValue": 78441,
                "minBuyout": 74989,
                "dealDiff": 3452,
                "discountPercentage": 4.40,
                "itemName": "Large Prismatic Shard"
                },
                {
                      "itemId": 22445,
                      "marketValue": 24762,
                      "minBuyout": 9598,
                      "dealDiff": 15164,
                      "discountPercentage": 61.24,
                      "itemName": "Arcane Dust"
                    }
                ]
                }
                """;
    }
}