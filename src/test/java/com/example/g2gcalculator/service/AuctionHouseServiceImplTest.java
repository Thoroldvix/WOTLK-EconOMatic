package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.AuctionHouseResponse;
import com.example.g2gcalculator.mapper.AuctionHouseMapper;
import com.example.g2gcalculator.model.AuctionHouse;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.model.Type;
import com.example.g2gcalculator.repository.AuctionHouseRepository;
import com.example.g2gcalculator.service.impl.AuctionHouseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionHouseServiceImplTest {

    @Mock
    AuctionHouseRepository auctionHouseRepository;

    @Mock
    AuctionHouseMapper auctionHouseMapper;

    @InjectMocks
    AuctionHouseServiceImpl auctionHouseService;

    @Test
    public void getAuctionHousesByRealmId_validId_shouldWork() {
        Integer realmId = 1;
        Realm mockRealm = Realm.builder().id(realmId).name("Everlook").build();

        List<AuctionHouse> mockAuctionHouses = List.of(
                new AuctionHouse(1, Type.ALLIANCE, mockRealm));

        when(auctionHouseRepository.findAllByRealmId(realmId)).thenReturn(mockAuctionHouses);

        List<AuctionHouseResponse> mockResponses = List.of(
                new AuctionHouseResponse(1, Type.ALLIANCE.toString()));

        when(auctionHouseMapper.toAuctionHouseResponse(any(AuctionHouse.class)))
                .thenAnswer(invocation -> {
                    AuctionHouse ah = invocation.getArgument(0);
                    return new AuctionHouseResponse(ah.getId(), ah.getType().toString());
                });


        List<AuctionHouseResponse> result = auctionHouseService.getAuctionHousesByRealmId(realmId);


        assertNotNull(result);
        assertEquals(mockResponses, result);
    }

    @Test
    public void getAuctionHousesByRealmId_invalidId_shouldWork() {

        Integer realmId = 999;

        when(auctionHouseRepository.findAllByRealmId(realmId)).thenReturn(Collections.emptyList());

        List<AuctionHouseResponse> result = auctionHouseService.getAuctionHousesByRealmId(realmId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAuctionHousesByRealmId_callsRepo() {

        Integer realmId = 123;

        auctionHouseService.getAuctionHousesByRealmId(realmId);

        verify(auctionHouseRepository).findAllByRealmId(realmId);
        verifyNoMoreInteractions(auctionHouseRepository);
    }

    @Test
    public void getAuctionHousesByRealmId_callsMapper() {
        Integer realmId = 456;
        Realm mockRealm = Realm.builder().id(realmId).name("Everlook").build();
        List<AuctionHouse> mockAuctionHouses = List.of(
                new AuctionHouse(1, Type.ALLIANCE, mockRealm));

        when(auctionHouseRepository.findAllByRealmId(realmId)).thenReturn(mockAuctionHouses);

        auctionHouseService.getAuctionHousesByRealmId(realmId);

        verify(auctionHouseMapper, times(mockAuctionHouses.size())).toAuctionHouseResponse(any(AuctionHouse.class));
        verifyNoMoreInteractions(auctionHouseMapper);
    }
}