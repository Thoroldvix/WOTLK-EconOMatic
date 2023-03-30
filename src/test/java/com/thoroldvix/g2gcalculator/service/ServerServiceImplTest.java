package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.server.*;
import com.thoroldvix.g2gcalculator.common.NotFoundException;
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
class ServerServiceImplTest {
    @Mock
    private ServerRepository serverRepository;
    @Mock
    private ServerMapper serverMapper;
    @InjectMocks
    private ServerServiceImpl realmService;


    @Test
    void getAllRealms_returnsRealmResponseList() {
        Server firstServer = Server.builder()
                .name("everlook")
                .faction(Faction.HORDE)
                .build();
        Server secondServer = Server.builder()
                .name("gehennas")
                .faction(Faction.ALLIANCE)
                .build();
        ServerResponse firstServerResponse = ServerResponse.builder()
                .name("everlook")
                .faction(Faction.HORDE)
                .build();
        ServerResponse secondServerResponse = ServerResponse.builder()
                .name("gehennas")
                .faction(Faction.ALLIANCE)
                .build();
        Page<Server> realms = new PageImpl<>(List.of(firstServer, secondServer));
        List<ServerResponse> expectedResponse = List.of(firstServerResponse, secondServerResponse);
        Pageable pageable = PageRequest.of(0, 10);
        when(serverRepository.findAll(pageable)).thenReturn(realms);
        when(serverMapper.toServerResponse(firstServer)).thenReturn(firstServerResponse);
        when(serverMapper.toServerResponse(secondServer)).thenReturn(secondServerResponse);

        List<ServerResponse> actualResponse = realmService.getAllServers(pageable);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getRealmResponse_returnsRealmResponse() {
        String realmName = "everlook-horde";
        Server server = Server.builder()
                .name("everlook")
                .faction(Faction.HORDE)
                .build();
        ServerResponse expectedResponse = ServerResponse.builder()
                .name("everlook")
                .faction(Faction.HORDE)
                .build();


        when(serverRepository.findByNameAndFaction(anyString(), any(Faction.class))).thenReturn(Optional.of(server));
        when(serverMapper.toServerResponse(server)).thenReturn(expectedResponse);

        ServerResponse actualResponse = realmService.getServerResponse(realmName);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getRealm_returnsRealmResponse() {
        String realmName = "everlook-alliance";
        Server expectedResponse = Server.builder()
                .name("everlook")
                .faction(Faction.ALLIANCE)
                .build();

        when(serverRepository.findByNameAndFaction(anyString(), any(Faction.class)))
                .thenReturn(Optional.of(expectedResponse));


        Server actualResponse = realmService.getServer(realmName);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getRealm_whenRealmNotFound_throwsNotFound() {
        String realmName = "everlook-alliance";

        when(serverRepository.findByNameAndFaction(anyString(), any(Faction.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> realmService.getServer(realmName))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRealmResponse_whenRealmNotFound_throwsNotFound() {
        String realmName = "everlook-alliance";
        Server server = Server.builder()
                .name("everlook")
                .faction(Faction.ALLIANCE)
                .build();

        when(serverRepository.findByNameAndFaction(server.getName(), server.getFaction())).thenReturn(Optional.empty());


        assertThatThrownBy(() -> realmService.getServerResponse(realmName))
                .isInstanceOf(NotFoundException.class);

    }

    @Test
    void getRealm_whenRealmNameIsNull_throwsIllegalArgumentException() {
        String realmName = null;
        assertThatThrownBy(() -> realmService.getServer(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealm_whenRealmNameIsEmpty_throwsIllegalArgumentException() {
        String realmName = "";
        assertThatThrownBy(() -> realmService.getServer(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealm_whenFactionHasInvalidValue_throwsNotFoundException() {
        String realmName = "everlook-a";
        assertThatThrownBy(() -> realmService.getServer(realmName))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRealmResponse_whenRealmNameIsEmpty_throwsIllegalArgumentException() {
        String realmName = "";
        assertThatThrownBy(() -> realmService.getServerResponse(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealmResponse_whenRealmNameIsBlank_throwsIllegalArgumentException() {
        String realmName = " ";
        assertThatThrownBy(() -> realmService.getServerResponse(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealm_whenRealmNameIsBlank_throwsIllegalArgumentException() {
        String realmName = " ";
        assertThatThrownBy(() -> realmService.getServer(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRealmResponse_whenFactionHasInvalidValue_throwsNotFoundException() {
        String realmName = "everlook-a";
        assertThatThrownBy(() -> realmService.getServerResponse(realmName))
                .isInstanceOf(NotFoundException.class);
    }

}