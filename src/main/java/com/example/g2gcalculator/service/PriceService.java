package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.model.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public interface PriceService {


    PriceResponse getPriceByRealmName(String realmName, Faction faction);


    List<PriceResponse> getAllPricesForRealm(String realmName, Faction faction, Pageable pageable);
}