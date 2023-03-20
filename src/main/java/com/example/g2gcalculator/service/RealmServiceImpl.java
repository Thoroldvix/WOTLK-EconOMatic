package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.mapper.RealmMapper;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.repository.RealmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealmServiceImpl implements RealmService {

    private final RealmRepository realmRepository;

    private final RealmMapper realmMapper;


    @Override
    public List<RealmResponse> getAllRealms() {
        return realmRepository.findAllFetch().stream()
                .map(realmMapper::toRealmResponse)
                .toList();
    }



    @Override
    public RealmResponse getRealmByName(String region, String realm) {
        return null;
    }
}