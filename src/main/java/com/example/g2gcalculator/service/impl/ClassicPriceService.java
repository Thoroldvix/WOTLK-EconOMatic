package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.mapper.PriceMapper;
import com.example.g2gcalculator.model.Price;
import com.example.g2gcalculator.repository.ClassicPriceRepository;
import com.example.g2gcalculator.service.PriceService;
import com.example.g2gcalculator.service.PriceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassicPriceService implements PriceService {
    private final ClassicPriceRepository ClassicPriceRepository;
    private final PriceMapper priceMapper;

    @Override
    public PriceResponse getPriceByRealmId(Integer realmId) {
        Price recentPrice = ClassicPriceRepository.findMostRecentByRealmId(realmId)
                .orElseThrow(() -> new RuntimeException("No price found for realmId: " + realmId));

        return priceMapper.toPriceResponse(recentPrice);
    }

    @Override
    public List<PriceResponse> getAllPricesForRealm(Integer realmId, Pageable pageable) {
        Specification<Price> spec = PriceSpecification.matchId(realmId);
        return ClassicPriceRepository.findAll(spec, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();

    }


}