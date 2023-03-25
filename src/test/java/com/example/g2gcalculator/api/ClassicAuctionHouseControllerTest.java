package com.example.g2gcalculator.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(ClassicAuctionHouseController.class)
@ActiveProfiles("test")

class ClassicAuctionHouseControllerTest {
    private final String API_AUCTION_HOUSE = "/wow-classic/v1/ah";

    @Test
    void getAllAuctionHouseItems_returnsAllAuctionHouseItems() {

    }
}