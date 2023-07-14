package com.thoroldvix.economatic.deal;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemDealsController.class)
class ItemDealsControllerTest extends BaseItemDealTest {

    public static final String ITEM_DEALS_API_ENDPOINT = "/wow-classic/api/v1/items/deals";


    @MockBean
    private ItemDealsServiceImpl itemDealsServiceImpl;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDealsForServer_returnsCorrectItemDealsResponse_whenServerIdentifierIsValid() throws Exception {
        String expectedJsonResponse = """
                {
                    "deals": [{
                        "itemId": 22449,
                        "marketValue": 78441,
                        "minBuyout": 74989,
                        "dealDiff": 3452,
                        "discountPercentage": 4.40,
                        "itemName": "Large Prismatic Shard"
                    }, {
                        "itemId": 22445,
                        "marketValue": 24762,
                        "minBuyout": 9598,
                        "dealDiff": 15164,
                        "discountPercentage": 61.24,
                        "itemName": "Arcane Dust"
                    }]
                }""";
        ItemDealsRequest itemDealsRequest = new ItemDealsRequest(SERVER_NAME, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT);
        ItemDealsList itemDeals = getItemDealsList();
        when(itemDealsServiceImpl.getDealsForServer(itemDealsRequest)).thenReturn(itemDeals);
        mockMvc.perform(get(ITEM_DEALS_API_ENDPOINT + "/" + SERVER_NAME))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse));
    }

    @Test
    void getDealsForServer_returnsBadRequest_whenServiceThrowsConstraintViolationException() throws Exception {
        ItemDealsRequest itemDealsRequest = new ItemDealsRequest(SERVER_NAME, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT);
        when(itemDealsServiceImpl.getDealsForServer(itemDealsRequest))
                .thenThrow(ConstraintViolationException.class);
        mockMvc.perform(get(ITEM_DEALS_API_ENDPOINT + "/" + SERVER_NAME))
                .andExpect(status().isBadRequest());
    }


}