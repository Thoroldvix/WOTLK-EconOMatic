package com.example.g2gcalculator.service;

import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.error.NotFoundException;
import com.example.g2gcalculator.mapper.RealmMapper;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.repository.ClassicRealmRepository;
import com.example.g2gcalculator.service.impl.ClassicRealmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.example.g2gcalculator.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassicRealmServiceTest {
    @Mock
    private ClassicRealmRepository classicRealmRepository;
    @Mock
    private RealmMapper realmMapper;
    @InjectMocks
    private ClassicRealmService realmService;


    @Test
    void getAllRealms_returnsRealmResponseList() {
        List<RealmResponse> expectedResponse =  List.of(createRealmResponse(1), createRealmResponse(2));
        Page<Realm> mockRealmPage = new PageImpl<>(List.of(createRealm(1), createRealm(2)));

        when(classicRealmRepository.findAll(any(Pageable.class))).thenReturn(mockRealmPage);
        when(realmMapper.toRealmResponse(mockRealmPage.getContent().get(0))).thenReturn(expectedResponse.get(0));
        when(realmMapper.toRealmResponse(mockRealmPage.getContent().get(1))).thenReturn(expectedResponse.get(1));

        List<RealmResponse> result = realmService.getAllRealms(PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expectedResponse.size());
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getRealm_returnsCorrectRealmResponse() {
        RealmResponse mockRealmResponse = createRealmResponse(1);
        Realm realm = createRealm(1);


        when(realmMapper.toRealmResponse(realm)).thenReturn(mockRealmResponse);
        when(classicRealmRepository.findByNameAndFaction(realm.getName(), realm.getFaction())).thenReturn(Optional.of(realm));

        RealmResponse result = realmService.getRealm(getFullRealmName(realm));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockRealmResponse);
    }

    @Test
    void getRealm_whenRealmNotFound_throwsNotFound() {
        Realm realm = createRealm(1);
        when(classicRealmRepository.findByNameAndFaction(realm.getName(), realm.getFaction())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> realmService.getRealm(getFullRealmName(realm)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No realm found for name: " + realm.getName() + " and faction: " + realm.getFaction());

    }
}