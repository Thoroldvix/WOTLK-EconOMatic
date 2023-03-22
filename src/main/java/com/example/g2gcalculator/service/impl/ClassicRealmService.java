package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.mapper.RealmMapper;
import com.example.g2gcalculator.repository.ClassicRealmRepository;
import com.example.g2gcalculator.service.RealmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicRealmService implements RealmService {

    private final ClassicRealmRepository classicRealmRepository;

    private final RealmMapper realmMapper;


    @Override
    public List<RealmResponse> getAllRealms() {
        return classicRealmRepository.findAllFetch().stream()
                .map(realmMapper::toRealmResponse)
                .toList();
    }

    @Override
    public RealmResponse getRealmById(Integer realmId) {
        return classicRealmRepository.findById(realmId)
                .map(realmMapper::toRealmResponse)
                .orElseThrow(() -> new NotFoundException("No realm found for id: " + realmId));
    }

}