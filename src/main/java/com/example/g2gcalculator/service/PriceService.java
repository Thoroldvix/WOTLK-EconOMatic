package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.model.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public interface PriceService {

    PriceResponse getPriceByRealmId(Integer realmId);


    List<PriceResponse> getAllPricesForRealm(Integer realmId, Pageable pageable);
}