package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.AuctionHouseResponse;
import org.springframework.stereotype.Component;

import java.util.List;


public interface AuctionHouseService {

    List<AuctionHouseResponse> getAuctionHousesByRealmId(Integer realmId);
}