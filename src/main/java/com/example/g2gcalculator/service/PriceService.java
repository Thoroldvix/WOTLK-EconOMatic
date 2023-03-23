package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Faction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {


    PriceResponse getPriceForRealm(String realmName);


    List<PriceResponse> getAllPricesForRealm(String realmName, Pageable pageable);
}