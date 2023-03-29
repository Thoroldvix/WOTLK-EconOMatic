package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.dto.RealmResponse;
import com.thoroldvix.g2gcalculator.error.NotFoundException;
import com.thoroldvix.g2gcalculator.mapper.RealmMapper;
import com.thoroldvix.g2gcalculator.model.Faction;
import com.thoroldvix.g2gcalculator.model.Realm;
import com.thoroldvix.g2gcalculator.repository.ClassicRealmRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        Realm firstRealm = Realm.builder()
                .name("everlook")
                .faction(Faction.HORDE)
                .build();
        Realm secondRealm = Realm.builder()
                .name("gehennas")
                .faction(Faction.ALLIANCE)
                .build();
        RealmResponse firstRealmResponse = RealmResponse.builder()
                .name("everlook")
                .faction(Faction.HORDE)
                .build();
        RealmResponse secondRealmResponse = RealmResponse.builder()
                .name("gehennas")
                .faction(Faction.ALLIANCE)
                .build();
        Page<Realm> realms = new PageImpl<>(List.of(firstRealm, secondRealm));
        List<RealmResponse> expectedResponse = List.of(firstRealmResponse, secondRealmResponse);
        Pageable pageable = PageRequest.of(0, 10);
        when(classicRealmRepository.findAll(pageable)).thenReturn(realms);
        when(realmMapper.toRealmResponse(firstRealm)).thenReturn(firstRealmResponse);
        when(realmMapper.toRealmResponse(secondRealm)).thenReturn(secondRealmResponse);

        List<RealmResponse> actualResponse = realmService.getAllRealms(pageable);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getRealmResponse_returnsRealmResponse() {
        String realmName = "everlook-horde";
        Realm realm = Realm.builder()
                .name("everlook")
                .faction(Faction.HORDE)
                .build();
        RealmResponse expectedResponse = RealmResponse.builder()
                .name("everlook")
                .faction(Faction.HORDE)
                .build();


        when(classicRealmRepository.findByNameAndFaction(anyString(), any(Faction.class))).thenReturn(Optional.of(realm));
        when(realmMapper.toRealmResponse(realm)).thenReturn(expectedResponse);

        RealmResponse actualResponse = realmService.getRealmResponse(realmName);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getRealm_returnsRealmResponse() {
        String realmName = "everlook-alliance";
        Realm expectedResponse = Realm.builder()
                .name("everlook")
                .faction(Faction.ALLIANCE)
                .build();

        when(classicRealmRepository.findByNameAndFaction(anyString(), any(Faction.class)))
                .thenReturn(Optional.of(expectedResponse));


        Realm actualResponse = realmService.getRealm(realmName);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getRealm_whenRealmNotFound_throwsNotFound() {
        String realmName = "everlook-alliance";

        when(classicRealmRepository.findByNameAndFaction(anyString(), any(Faction.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> realmService.getRealm(realmName))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRealmResponse_whenRealmNotFound_throwsNotFound() {
        String realmName = "everlook-alliance";
        Realm realm = Realm.builder()
                .name("everlook")
                .faction(Faction.ALLIANCE)
                .build();

        when(classicRealmRepository.findByNameAndFaction(realm.getName(), realm.getFaction())).thenReturn(Optional.empty());


        assertThatThrownBy(() -> realmService.getRealmResponse(realmName))
                .isInstanceOf(NotFoundException.class);

    }

    @Test
    void getRealm_whenRealmNameIsNull_throwsIllegalArgumentException() {
        String realmName = null;
        assertThatThrownBy(() -> realmService.getRealm(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealm_whenRealmNameIsEmpty_throwsIllegalArgumentException() {
        String realmName = "";
        assertThatThrownBy(() -> realmService.getRealm(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealm_whenFactionHasInvalidValue_throwsNotFoundException() {
        String realmName = "everlook-a";
        assertThatThrownBy(() -> realmService.getRealm(realmName))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRealmResponse_whenRealmNameIsEmpty_throwsIllegalArgumentException() {
        String realmName = "";
        assertThatThrownBy(() -> realmService.getRealmResponse(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealmResponse_whenRealmNameIsBlank_throwsIllegalArgumentException() {
        String realmName = " ";
        assertThatThrownBy(() -> realmService.getRealmResponse(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealm_whenRealmNameIsBlank_throwsIllegalArgumentException() {
        String realmName = " ";
        assertThatThrownBy(() -> realmService.getRealm(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealmResponse_whenFactionHasInvalidValue_throwsNotFoundException() {
        String realmName = "everlook-a";
        assertThatThrownBy(() -> realmService.getRealmResponse(realmName))
                .isInstanceOf(NotFoundException.class);
    }

}