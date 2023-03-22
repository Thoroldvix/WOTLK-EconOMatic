package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.PriceResponse;
import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.mapper.RealmMapper;
import com.example.g2gcalculator.model.GameVersion;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.repository.ClassicRealmRepository;
import com.example.g2gcalculator.service.impl.ClassicRealmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassicRealmServiceTest {
    @Mock
    private ClassicRealmRepository ClassicRealmRepository;
    @Mock
    private RealmMapper realmMapper;
    @InjectMocks
    private ClassicRealmService realmService;


    @Test
    void getAllRealms_shouldWork() {
        RealmResponse mockRealmResponse = new RealmResponse(1, "Everlook", new PriceResponse(BigDecimal.valueOf(0.5)),
                GameVersion.CLASSIC.name(), Collections.emptyList());


        when(realmMapper.toRealmResponse(any(Realm.class))).thenReturn(mockRealmResponse);
        List<Realm> mockResult = List.of(Realm.builder().id(1).name("Everlook").build());

        when(ClassicRealmRepository.findAllFetch()).thenReturn(mockResult);

        List<RealmResponse> result = realmService.getAllRealms();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).isEqualTo(List.of(mockRealmResponse));
    }

    @Test
    void getAllRealms_callsRepo() {
        realmService.getAllRealms();

        verify(ClassicRealmRepository).findAllFetch();
        verifyNoMoreInteractions(ClassicRealmRepository);
    }

    @Test
    void getAllRealms_callsMapper() {
        RealmResponse mockRealmResponse = new RealmResponse(1, "Everlook", new PriceResponse(BigDecimal.valueOf(0.5)), GameVersion.CLASSIC.name(),
                Collections.emptyList());
        List<Realm> mockResult = List.of(Realm.builder().id(1).name("Everlook").build());

        when(ClassicRealmRepository.findAllFetch()).thenReturn(mockResult);
        when(realmMapper.toRealmResponse(any(Realm.class))).thenReturn(mockRealmResponse);
        realmService.getAllRealms();

        verify(realmMapper, times(mockResult.size())).toRealmResponse(any(Realm.class));
        verifyNoMoreInteractions(realmMapper);
    }
}