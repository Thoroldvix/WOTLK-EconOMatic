package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.api.AuctionHouseClient;
import com.thoroldvix.g2gcalculator.mapper.AuctionHouseMapper;
import com.thoroldvix.g2gcalculator.repository.AuctionHouseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClassicAuctionHouseServiceTest {
    @Mock
    private AuctionHouseClient auctionHouseClient;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AuctionHouseRepository auctionHouseRepository;
    @Mock
    private AuctionHouseMapper auctionHouseMapper;
    @InjectMocks
    private ClassicAuctionHouseService classicAuctionHouseService;



}