package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerResponse;
import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerRepository;
import com.thoroldvix.pricepal.server.error.ServerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
    void getServer_whenServerIdValid_thenReturnServer() {
        Server expectedResponse = Server.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();

        when(serverRepository.findById(expectedResponse.getId())).thenReturn(Optional.of(expectedResponse));
        assertThat(serverServiceImpl.getServer(expectedResponse.getId())).isEqualTo(expectedResponse);
    }

    @Test
    void getServer_whenServerIdInvalid_throwsIllegalArgumentException() {
        int negativeId = -1;
        int zeroId = 0;
        assertThatThrownBy(() -> serverServiceImpl.getServer(negativeId)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> serverServiceImpl.getServer(zeroId)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getServer_whenServerWithIdNotFound_throwsServerNotFoundException() {
        int id = 1;
        when(serverRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serverServiceImpl.getServer(id)).isInstanceOf(ServerNotFoundException.class);
    }

    @Test
    void getServer_whenUniqueServerNameProvided_thenReturnServer() {
        Server expectedResponse = Server.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();

        when(serverRepository.findByUniqueName(expectedResponse.getUniqueName())).thenReturn(Optional.of(expectedResponse));
        assertThat(serverServiceImpl.getServer(expectedResponse.getUniqueName())).isEqualTo(expectedResponse);
    }

    @Test
    void getServer_whenUniqueServerNameInvalid_throwsIllegalArgumentException() {
        String blankName = " ";
        String emptyName = "";
        String nullName = null;
        assertThatThrownBy(() -> serverServiceImpl.getServer(blankName)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> serverServiceImpl.getServer(emptyName)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> serverServiceImpl.getServer(nullName)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getServer_whenUniqueServerNameNotFound_throwsServerNotFoundException() {
        String uniqueName = "everlook-hord";
        when(serverRepository.findByUniqueName(uniqueName)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serverServiceImpl.getServer(uniqueName)).isInstanceOf(ServerNotFoundException.class);
    }

    @Test
    void getServerResponse_whenServerIdProvided_thenReturnServerResponse() {
        Server server = Server.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();
        ServerResponse expectedResponse = ServerResponse.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();

        when(serverRepository.findById(server.getId())).thenReturn(Optional.of(server));
        when(serverMapper.toServerResponse(server)).thenReturn(expectedResponse);
        assertThat(serverServiceImpl.getServerResponse(server.getId())).isEqualTo(expectedResponse);
    }

    @Test
    void getServerResponse_whenServerIdInvalid_throwsIllegalArgumentException() {
        int negativeId = -1;
        int zeroId = 0;
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(negativeId)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(zeroId)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void getServerResponse_whenServerWithIdNotFound_throwsServerNotFoundException() {
        int id = 1;
        when(serverRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(id)).isInstanceOf(ServerNotFoundException.class);
    }
    @Test
    void getServerResponse_whenUniqueServerNameProvided_thenReturnServerResponse() {
         Server server = Server.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();
        ServerResponse expectedResponse = ServerResponse.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();

        when(serverRepository.findByUniqueName(server.getUniqueName())).thenReturn(Optional.of(server));
        when(serverMapper.toServerResponse(server)).thenReturn(expectedResponse);
        assertThat(serverServiceImpl.getServerResponse(server.getUniqueName())).isEqualTo(expectedResponse);
    }
    @Test
    void getServerResponse_whenUniqueServerNameInvalid_throwsIllegalArgumentException() {
        String blankName = " ";
        String emptyName = "";
        String nullName = null;
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(blankName)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(emptyName)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> serverServiceImpl.getServerResponse(nullName)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void getServerResponse_whenUniqueServerNameNotFound_throwsServerNotFoundException() {
        String uniqueName = "everlook-hord";

        when(serverRepository.findByUniqueName(uniqueName)).thenReturn(Optional.empty());
        assertThatThrownBy( () -> serverServiceImpl.getServerResponse(uniqueName)).isInstanceOf(ServerNotFoundException.class);
    }
    @Test
    void getAllServers_whenPageableProvided_thenReturnListOfServerResponses() {
        Server server = Server.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();
        ServerResponse expectedResponse = ServerResponse.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();

        List<Server> servers = List.of(server);


        when(serverRepository.findAll()).thenReturn(servers);
        when(serverMapper.toServerResponse(server)).thenReturn(expectedResponse);
        assertThat(serverServiceImpl.getAllServers()).containsExactly(expectedResponse);
    }
    @Test
    void getAllServers_whenNoServersFound_throwsServerNotFoundException() {
        when(serverRepository.findAll()).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> serverServiceImpl.getAllServers())
                .isInstanceOf(ServerNotFoundException.class);
    }

    @Test
    void getAllServersForRegion_whenRegionProvided_thenReturnListOfServerResponses() {
        Server server = Server.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();
        ServerResponse expectedResponse = ServerResponse.builder()
                .id(1)
                .name("everlook")
                .uniqueName("everlook-horde")
                .region(Region.EU)
                .faction(Faction.HORDE)
                .build();
        List<Server> servers = List.of(server);

        when(serverRepository.findAllByRegion(server.getRegion())).thenReturn(servers);
        when(serverMapper.toServerResponse(server)).thenReturn(expectedResponse);
        assertThat(serverServiceImpl.getAllServersForRegion(server.getRegion())).containsExactly(expectedResponse);
    }
    @Test
    void getAllServersForRegion_whenRegionIsNull_throwsNullPointerException() {
        Region invalidRegion = null;
        assertThatThrownBy(() -> serverServiceImpl.getAllServersForRegion(invalidRegion))
                .isInstanceOf(NullPointerException.class);
    }
    @Test
    void getAllServersForRegion_whenNoServersFound_throwsServerNotFoundException() {
        Region region = Region.EU;
        when(serverRepository.findAllByRegion(region)).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> serverServiceImpl.getAllServersForRegion(region))
                .isInstanceOf(ServerNotFoundException.class);
    }

}