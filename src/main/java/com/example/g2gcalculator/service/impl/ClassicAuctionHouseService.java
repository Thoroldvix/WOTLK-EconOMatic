package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.api.AuctionHouseClient;
import com.example.g2gcalculator.dto.AuctionHouseResponse;
import com.example.g2gcalculator.dto.ItemResponse;
import com.example.g2gcalculator.mapper.AuctionHouseMapper;
import com.example.g2gcalculator.repository.AuctionHouseRepository;
import com.example.g2gcalculator.service.AuctionHouseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicAuctionHouseService implements AuctionHouseService {
    private final AuctionHouseClient auctionHouseClient;

    private final ObjectMapper objectMapper;
    private final AuctionHouseRepository auctionHouseRepository;

    private final AuctionHouseMapper auctionHouseMapper;
    @Override
    public List<AuctionHouseResponse> getAuctionHousesByRealmId(Integer realmId) {
        return auctionHouseRepository.findAllByRealmId(realmId).stream()
                .map(auctionHouseMapper::toAuctionHouseResponse)
                .toList();
    }

    @Override
    public List<ItemResponse> getAllItemsByAuctionHouseId(Integer auctionHouseId) {
        try {
            return objectMapper.readValue(auctionHouseClient.getAllAuctionHouseItems(auctionHouseId), new TypeReference<List<ItemResponse>>() {});
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}