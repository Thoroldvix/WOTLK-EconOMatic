package com.thoroldvix.g2gcalculator.api;

import com.thoroldvix.g2gcalculator.dto.ItemResponse;
import com.thoroldvix.g2gcalculator.service.AuctionHouseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClassicAuctionHouseController.class)
@ActiveProfiles("test")
class ClassicAuctionHouseControllerTest {
    private final String API_AUCTION_HOUSE = "/wow-classic/v1/ah";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuctionHouseService classicAuctionHouseService;

    @Test
    void getAllAuctionHouseItems_whenValidAuctionHouseId_returnsListOfItemResponse() throws Exception {
        ItemResponse firstItem = ItemResponse.builder()
                .itemId(1)
                .minBuyout(100L)
                .build();
        ItemResponse secondItem = ItemResponse.builder()
                .itemId(2)
                .minBuyout(200L)
                .build();
        List<ItemResponse> expectedResponse = List.of(firstItem, secondItem);
        int auctionHouseId = 279;

        when(classicAuctionHouseService.getAllItemsByAuctionHouseId(auctionHouseId)).thenReturn(expectedResponse);

        mockMvc.perform(get(API_AUCTION_HOUSE + "/{auctionHouseId}", auctionHouseId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void getAllAuctionHouseItems_whenAuctionHouseIdGreaterThanMax_returnsNotFound() throws Exception {
        int auctionHouseId = 10000;
        mockMvc.perform(get(API_AUCTION_HOUSE + "/{auctionHouseId}", auctionHouseId))
                .andExpect(status().isNotFound());
    }
     @Test
    void getAllAuctionHouseItems_whenAuctionHouseIdLessThanMin_returnsNotFound() throws Exception {
        int auctionHouseId = 278;
        mockMvc.perform(get(API_AUCTION_HOUSE + "/{auctionHouseId}", auctionHouseId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAuctionHouseItem_whenValidAuctionHouseIdAndItemId_returnsItemResponse() throws Exception {
        int auctionHouseId = 279;
        int itemId = 1;
        ItemResponse expectedResponse = ItemResponse.builder()
                .itemId(itemId)
                .minBuyout(100L)
                .build();

        when(classicAuctionHouseService.getAuctionHouseItem(auctionHouseId, itemId)).thenReturn(expectedResponse);
        mockMvc.perform(get(API_AUCTION_HOUSE + "/{auctionHouseId}/items/{itemId}", auctionHouseId, itemId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }
     @Test
    void getAuctionHouseItem_whenAuctionHouseIdGreaterThanMax_returnsNotFound() throws Exception {
        int auctionHouseId = 10000;
        int itemId = 1;
        mockMvc.perform(get(API_AUCTION_HOUSE + "/{auctionHouseId}/items/{itemId}", auctionHouseId, itemId))
                .andExpect(status().isNotFound());
    }
      @Test
    void getAuctionHouseItem_whenAuctionHouseIdLessThanMin_returnsNotFound() throws Exception {
        int auctionHouseId = 278;
        int itemId = 1;
        mockMvc.perform(get(API_AUCTION_HOUSE + "/{auctionHouseId}/items/{itemId}", auctionHouseId, itemId))
                .andExpect(status().isNotFound());
    }


    @Test
    void getAuctionHouseItem_whenInvalidItemId_returnsNotFound() throws Exception {
        int auctionHouseId = 279;
        int itemId = -1;


        mockMvc.perform(get(API_AUCTION_HOUSE + "/{auctionHouseId}/items/{itemId}", auctionHouseId, itemId))
                .andExpect(status().isNotFound());
    }


}