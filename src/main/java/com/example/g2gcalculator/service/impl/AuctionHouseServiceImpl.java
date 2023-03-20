package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.dto.AuctionHouseResponse;
import com.example.g2gcalculator.mapper.AuctionHouseMapper;
import com.example.g2gcalculator.model.AuctionHouse;
import com.example.g2gcalculator.repository.AuctionHouseRepository;
import com.example.g2gcalculator.service.AuctionHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionHouseServiceImpl implements AuctionHouseService {

    private final AuctionHouseRepository auctionHouseRepository;

    private final AuctionHouseMapper auctionHouseMapper;
    @Override
    public List<AuctionHouseResponse> getAuctionHousesByRealmId(Integer realmId) {
        return auctionHouseRepository.findAllByRealmId(realmId).stream()
                .map(auctionHouseMapper::toAuctionHouseResponse)
                .toList();
    }
}