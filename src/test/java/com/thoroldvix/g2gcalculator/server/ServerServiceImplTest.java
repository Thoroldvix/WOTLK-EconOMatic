package com.thoroldvix.g2gcalculator.server;

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
    private ServerServiceImpl serverServiceImpl;


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

        List<ServerResponse> actualResponse = serverServiceImpl.getAllServers(pageable);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getServerResponse_returnsRealmResponse() {
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

        ServerResponse actualResponse = serverServiceImpl.getServerResponse(realmName);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getServer_returnsRealmResponse() {
        String realmName = "everlook-alliance";
        Server expectedResponse = Server.builder()
                .name("everlook")
                .faction(Faction.ALLIANCE)
                .build();

        when(serverRepository.findByNameAndFaction(anyString(), any(Faction.class)))
                .thenReturn(Optional.of(expectedResponse));


        Server actualResponse = serverServiceImpl.getServer(realmName);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getServer_whenRealmNotFound_throwsNotFound() {
        String realmName = "everlook-alliance";

        when(serverRepository.findByNameAndFaction(anyString(), any(Faction.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serverServiceImpl.getServer(realmName))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getServerResponse_whenRealmNotFound_throwsNotFound() {
        String realmName = "everlook-alliance";
        Server server = Server.builder()
                .name("everlook")
                .faction(Faction.ALLIANCE)
                .build();

        when(serverRepository.findByNameAndFaction(server.getName(), server.getFaction())).thenReturn(Optional.empty());


        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(realmName))
                .isInstanceOf(NotFoundException.class);

    }

    @Test
    void getServer_whenRealmNameIsNull_throwsIllegalArgumentException() {
        String realmName = null;
        assertThatThrownBy(() -> serverServiceImpl.getServer(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getServer_whenRealmNameIsEmpty_throwsIllegalArgumentException() {
        String realmName = "";
        assertThatThrownBy(() -> serverServiceImpl.getServer(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getServer_whenFactionHasInvalidValue_throwsNotFoundException() {
        String realmName = "everlook-a";
        assertThatThrownBy(() -> serverServiceImpl.getServer(realmName))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getServerResponse_whenRealmNameIsEmpty_throwsIllegalArgumentException() {
        String realmName = "";
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getServerResponse_whenRealmNameIsBlank_throwsIllegalArgumentException() {
        String realmName = " ";
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getServer_whenRealmNameIsBlank_throwsIllegalArgumentException() {
        String realmName = " ";
        assertThatThrownBy(() -> serverServiceImpl.getServer(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getServerResponse_whenFactionHasInvalidValue_throwsIllegalArgumentException() {
        String realmName = "everlook-a";
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(realmName))
                .isInstanceOf(NotFoundException.class);
    }
    @Test
    void getServerResponse_whenFactionIsNotPresent_throwsIllegalArgumentException() {
        String realmName = "everlook-";
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }
     @Test
    void getServer_whenFactionIsNotPresent_throwsNotFoundException() {
        String realmName = "everlook-";
        assertThatThrownBy(() -> serverServiceImpl.getServer(realmName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getServerById_whenValidServerId_returnsCorrectServer() {
        Server expectedServer = Server.builder()
                .id(1)
                .name("everlook")
                .faction(Faction.HORDE)
                .build();
        when(serverRepository.findById(1)).thenReturn(Optional.of(expectedServer));

        Server actualServer = serverServiceImpl.getServerById(1);

        assertThat(actualServer).isEqualTo(expectedServer);
    }
    @Test
    void getAllForRegion_whenListOfRegionsGiven_returnsListOfCorrectServerResponse() {
        ServerResponse firstServer = ServerResponse.builder()
                .name("everlook")
                .region("EU")
                .faction(Faction.HORDE)
                .build();
        ServerResponse secondServer = ServerResponse.builder()
                .name("gehennas")
                .region("EU")
                .faction(Faction.ALLIANCE)
                .build();
        Server firstServerEntity = Server.builder()
                .name("everlook")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();
        Server secondServerEntity = Server.builder()
                .name("gehennas")
                .region(Region.EU)
                .faction(Faction.ALLIANCE)
                .build();
        List<Server> servers = List.of(firstServerEntity, secondServerEntity);
        List<Region> euRegions = Region.getEURegions();
        List<ServerResponse> expectedResponse = List.of(firstServer, secondServer);
        when(serverRepository.findAllByRegionIn(euRegions)).thenReturn(servers);
        when(serverMapper.toServerResponse(firstServerEntity)).thenReturn(firstServer);
        when(serverMapper.toServerResponse(secondServerEntity)).thenReturn(secondServer);


        List<ServerResponse> actualResponse = serverServiceImpl.getAllForRegion(euRegions);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
  @Test
    void getAllForRegion_whenSingleRegionGiven_returnsListOfCorrectServerResponse() {
        ServerResponse firstServer = ServerResponse.builder()
                .name("everlook")
                .region("EU")
                .faction(Faction.HORDE)
                .build();
        ServerResponse secondServer = ServerResponse.builder()
                .name("gehennas")
                .region("EU")
                .faction(Faction.ALLIANCE)
                .build();
        Server firstServerEntity = Server.builder()
                .name("everlook")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();
        Server secondServerEntity = Server.builder()
                .name("gehennas")
                .region(Region.EU)
                .faction(Faction.ALLIANCE)
                .build();
        List<Server> servers = List.of(firstServerEntity, secondServerEntity);
        List<ServerResponse> expectedResponse = List.of(firstServer, secondServer);
        when(serverRepository.findAllByRegionIn(List.of(Region.EU))).thenReturn(servers);
        when(serverMapper.toServerResponse(firstServerEntity)).thenReturn(firstServer);
        when(serverMapper.toServerResponse(secondServerEntity)).thenReturn(secondServer);


        List<ServerResponse> actualResponse = serverServiceImpl.getAllForRegion(Region.EU);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getAllServers_returnsListOfServerResponse() {
        ServerResponse firstServer = ServerResponse.builder()
                .name("everlook")
                .region("EU")
                .faction(Faction.HORDE)
                .build();
        ServerResponse secondServer = ServerResponse.builder()
                .name("gehennas")
                .region("EU")
                .faction(Faction.ALLIANCE)
                .build();
        Server firstServerEntity = Server.builder()
                .name("everlook")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();
        Server secondServerEntity = Server.builder()
                .name("gehennas")
                .region(Region.EU)
                .faction(Faction.ALLIANCE)
                .build();
        List<Server> servers = List.of(firstServerEntity, secondServerEntity);
        List<ServerResponse> expectedResponse = List.of(firstServer, secondServer);
        when(serverRepository.findAll()).thenReturn(servers);
        when(serverMapper.toServerResponse(firstServerEntity)).thenReturn(firstServer);
        when(serverMapper.toServerResponse(secondServerEntity)).thenReturn(secondServer);

        List<ServerResponse> actualResponse = serverServiceImpl.getAllServers();

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}