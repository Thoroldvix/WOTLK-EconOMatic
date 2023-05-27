package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.*;
import com.thoroldvix.pricepal.server.error.ServerPriceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ServerPriceServiceImplTest {

    public final Pageable pageable = Pageable.unpaged();
    @Mock
    private ServerPriceRepository serverPriceRepository;
    @Mock
    private ServerServiceImpl serverServiceImpl;
    @Mock
    private ServerPriceMapper serverPriceMapper;
    @InjectMocks
    private ServerPriceServiceImpl serverPriceServiceImpl;


    @Test
    void getPriceForServer_whenServerNameProvided_returnsValidServerPriceResponse() {
        Server server = Server.builder()
                .serverPrices(new ArrayList<>())
                .factionType(Faction.ALLIANCE)
                .uniqueName("everlook-alliance")
                .name("everlook")
                .build();
        ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .currency(Currency.USD)
                .build();
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();


        when(serverPriceRepository.findMostRecentPriceByUniqueServerName(server.getUniqueName())).thenReturn(Optional.of(serverPrice));
        when(serverPriceMapper.toPriceResponse(any(ServerPrice.class))).thenReturn(serverPriceResponse);

        ServerPriceResponse result = serverPriceServiceImpl.getPriceForServer(server.getUniqueName());
        assertThat(result.value()).isEqualTo(serverPriceResponse.value());
    }

    @Test
    void getPriceForServer_whenNoPriceFoundForServerName_throwsServerPriceNotFoundException() {
        String serverName = "everlook-alliance";
        when(serverPriceRepository.findMostRecentPriceByUniqueServerName(serverName)).thenReturn(Optional.empty());
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getPriceForServer(serverName));
    }

    @Test
    void getPriceForServer_whenServerNameInvalid_throwsIllegalArgumentException() {
        String blankServerName = " ";
        String nullServerName = null;
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getPriceForServer(blankServerName));
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getPriceForServer(nullServerName));
    }

    @Test
    void getPriceForServer_whenServerProvided_returnsValidPriceResponse() {
        Server server = Server.builder()
                .serverPrices(new ArrayList<>())
                .factionType(Faction.ALLIANCE)
                .name("everlook")
                .build();
        ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .currency(Currency.USD)
                .build();
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverPriceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.of(serverPrice));
        when(serverPriceMapper.toPriceResponse(any(ServerPrice.class))).thenReturn(serverPriceResponse);

        ServerPriceResponse result = serverPriceServiceImpl.getPriceForServer(server);
        assertThat(result.value()).isEqualTo(serverPriceResponse.value());
    }

    @Test
    void getPriceForServer_whenNoPriceFoundForServer_throwsServerPriceNotFoundException() {
        Server server = new Server();
        when(serverPriceRepository.findMostRecentPriceByServer(server)).thenReturn(Optional.empty());
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getPriceForServer(server));
    }

    @Test
    void getPriceForServer_whenServerIsNull_throwsNullPointerException() {
        Server server = null;
        assertThrows(NullPointerException.class, () -> serverPriceServiceImpl.getPriceForServer(server));
    }

    @Test
    void getPriceForServer_whenServerIdProvided_returnsValidServerPriceResponse() {
        Server server = Server.builder()
                .id(1)
                .serverPrices(new ArrayList<>())
                .factionType(Faction.ALLIANCE)
                .name("everlook")
                .build();
        ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .currency(Currency.USD)
                .build();
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverPriceRepository.findMostRecentPriceByServerId(server.getId())).thenReturn(Optional.of(serverPrice));
        when(serverPriceMapper.toPriceResponse(any(ServerPrice.class))).thenReturn(serverPriceResponse);

        ServerPriceResponse result = serverPriceServiceImpl.getPriceForServer(server.getId());

        assertThat(result.value()).isEqualTo(serverPriceResponse.value());
    }

    @Test
    void getPriceForServer_whenNoPriceFoundForServerId_throwsServerPriceNotFoundException() {
        int serverId = 1;
        when(serverPriceRepository.findMostRecentPriceByServerId(serverId)).thenReturn(Optional.empty());
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getPriceForServer(serverId));
    }

    @Test
    void getPriceForServer_whenServerIdInvalid_throwsIllegalArgumentException() {
        int negativeServerId = -1;
        int zeroServerId = 0;
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getPriceForServer(negativeServerId));
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getPriceForServer(zeroServerId));
    }

    @Test
    void getAllPricesForServer_whenServerNameProvided_returnsListOfPriceResponses() {
        Server server = Server.builder()
                .uniqueName("everlook-alliance")
                .name("everlook")
                .faction(Faction.ALLIANCE)
                .build();
        ServerPrice firstServerPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(100))
                .server(server)
                .build();
        ServerPrice secondServerPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .server(server)
                .build();
        Page<ServerPrice> prices = new PageImpl<>(List.of(firstServerPrice, secondServerPrice));
        ServerPriceResponse firstServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        ServerPriceResponse secondServerPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverPriceRepository.findAllByUniqueServerName(server.getUniqueName(), pageable)).thenReturn(prices);
        when(serverPriceMapper.toPriceResponse(firstServerPrice)).thenReturn(firstServerPriceResponse);
        when(serverPriceMapper.toPriceResponse(secondServerPrice)).thenReturn(secondServerPriceResponse);

        List<ServerPriceResponse> actualResponse = serverPriceServiceImpl
                .getAllPricesForServer(server.getUniqueName(), pageable);

        assertThat(actualResponse).containsExactly(firstServerPriceResponse, secondServerPriceResponse);
    }

    @Test
    void getAllPricesForServer_whenNoPricesFoundForServerName_throwsServerPriceNotFoundException() {
        String serverName = "everlook-alliance";
        when(serverPriceRepository.findAllByUniqueServerName(serverName, pageable)).thenReturn(new PageImpl<>(List.of()));
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getAllPricesForServer(serverName, pageable));
    }

    @Test
    void getAllPricesForServer_whenServerNameIsInvalid_throwsIllegalArgumentException() {
        String blankServerName = " ";
        String nullServerName = null;
        String emptyServerName = "";

        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAllPricesForServer(blankServerName, pageable));
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAllPricesForServer(nullServerName, pageable));
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAllPricesForServer(emptyServerName, pageable));
    }

    @Test
    void getAllPricesForServer_whenServerNameValidAndPageableIsNull_throwsNullPointerException() {
        String serverName = "everlook-alliance";
        assertThrows(NullPointerException.class, () -> serverPriceServiceImpl.getAllPricesForServer(serverName, null));
    }

    @Test
    void savePrice_whenValidParameters_savesPriceInDB() {
        Server server = Server.builder()
                .id(1)
                .serverPrices(new ArrayList<>())
                .factionType(Faction.ALLIANCE)
                .name("everlook")
                .build();
        ServerPrice serverPrice = ServerPrice.builder()
                .value(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .currency(Currency.USD)
                .build();
        ServerPriceResponse serverPriceResponse = ServerPriceResponse.builder()
                .value(BigDecimal.valueOf(200))
                .build();

        when(serverServiceImpl.getServer(1)).thenReturn(server);
        when(serverPriceMapper.toServerPrice(serverPriceResponse)).thenReturn(serverPrice);

        serverPriceServiceImpl.savePrice(1, serverPriceResponse);

        verify(serverPriceRepository, times(1)).save(serverPrice);
    }

    @Test
    void savePrice_whenInvalidServerId_throwsIllegalArgumentException() {
        int negativeServerId = -1;
        int zeroServerId = 0;
        ServerPriceResponse recentPrice = ServerPriceResponse.builder().build();
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.savePrice(negativeServerId, recentPrice));
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.savePrice(zeroServerId, recentPrice));
    }

    @Test
    void savePrice_whenRecentPriceIsNull_throwsNullPointerException() {
        int serverId = 1;
        assertThrows(NullPointerException.class, () -> serverPriceServiceImpl.savePrice(serverId, null));
    }

    @Test
    void getAvgPriceForRegion_whenValidRegion_returnsAvgPriceForRegion() {
        BigDecimal price = BigDecimal.valueOf(200);
        ServerPriceResponse expectedResult = ServerPriceResponse.builder()
                .currency(Currency.USD)
                .value(price)
                .build();

        when(serverPriceRepository.findAvgPriceByRegion(any(), any())).thenReturn(Optional.of(price));
        ServerPriceResponse actualResult = serverPriceServiceImpl.getAvgPriceForRegion(Region.EU);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAvgPriceForRegion_whenRegionIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> serverPriceServiceImpl.getAvgPriceForRegion(null));
    }

    @Test
    void getAvgPriceForRegion_whenNoPricesFoundForRegion_throwsServerPriceNotFoundException() {
        when(serverPriceRepository.findAvgPriceByRegion(any(), any())).thenReturn(Optional.empty());
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getAvgPriceForRegion(Region.EU));
    }

    @Test
    void getAllPricesForRegion_whenValidRegion_returnsListOfPriceResponses() {
        BigDecimal price = BigDecimal.valueOf(200);
        ServerPrice serverPrice = ServerPrice.builder()
                .value(price)
                .build();

        ServerPriceResponse expectedResult = ServerPriceResponse.builder()
                .currency(Currency.USD)
                .value(price)
                .build();

        when(serverPriceRepository.findAllPricesByRegion(Region.EU, pageable))
                .thenReturn(new PageImpl<>(List.of(serverPrice)));
        when(serverPriceMapper.toPriceResponse(serverPrice)).thenReturn(expectedResult);
        List<ServerPriceResponse> actualResult = serverPriceServiceImpl.getAllPricesForRegion(Region.EU, pageable);

        assertThat(actualResult).containsExactlyInAnyOrder(expectedResult)
                .extracting(ServerPriceResponse::value)
                .containsExactlyInAnyOrder(expectedResult.value());
    }

    @Test
    void getAllPricesForRegion_whenNoPricesFoundForRegion_throwsServerPriceNotFoundException() {
        when(serverPriceRepository.findAllPricesByRegion(Region.EU, pageable)).thenReturn(new PageImpl<>(List.of()));
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getAllPricesForRegion(Region.EU, pageable));
    }

    @Test
    void getAllPricesForRegion_whenRegionIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> serverPriceServiceImpl.getAllPricesForRegion(null, pageable));
    }

    @Test
    void getAllPricesForRegion_whenPageableIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> serverPriceServiceImpl.getAllPricesForRegion(Region.EU, null));
    }

    @Test
    void getAllPricesForFaction_whenValidFaction_returnsListOfPriceResponses() {
        BigDecimal price = BigDecimal.valueOf(200);
        ServerPrice serverPrice = ServerPrice.builder()
                .value(price)
                .build();
        ServerPriceResponse expectedResult = ServerPriceResponse.builder()
                .currency(Currency.USD)
                .value(price)
                .build();

        when(serverPriceRepository.findAllPricesByFaction(Faction.ALLIANCE, pageable))
                .thenReturn(new PageImpl<>(List.of(serverPrice)));
        when(serverPriceMapper.toPriceResponse(serverPrice)).thenReturn(expectedResult);
        List<ServerPriceResponse> actualResult = serverPriceServiceImpl.getAllPricesForFaction(Faction.ALLIANCE, pageable);

        assertThat(actualResult).containsExactlyInAnyOrder(expectedResult)
                .extracting(ServerPriceResponse::value)
                .containsExactlyInAnyOrder(expectedResult.value());
    }

    @Test
    void getAllPricesForFaction_whenFactionIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> serverPriceServiceImpl.getAllPricesForFaction(null, pageable));
    }

    @Test
    void getAllPricesForFaction_whenNoPricesFoundForFaction_throwsServerPriceNotFoundException() {
        when(serverPriceRepository.findAllPricesByFaction(Faction.ALLIANCE, pageable)).thenReturn(new PageImpl<>(List.of()));
        assertThrows(ServerPriceNotFoundException.class,
                () -> serverPriceServiceImpl.getAllPricesForFaction(Faction.ALLIANCE, pageable));
    }

    @Test
    void getAllPricesForFaction_whenPageableIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> serverPriceServiceImpl.getAllPricesForFaction(Faction.ALLIANCE, null));
    }


    @Test
    void getAvgPriceForFaction_whenValidFaction_returnsAvgPriceForFaction() {
        BigDecimal price = BigDecimal.valueOf(200).setScale(5, RoundingMode.HALF_UP);
        ServerPriceResponse expectedResult = ServerPriceResponse.builder()
                .currency(Currency.USD)
                .value(price)
                .build();

        when(serverPriceRepository.findAvgPriceByFaction(any(), any())).thenReturn(Optional.of(price));

        ServerPriceResponse actualResult = serverPriceServiceImpl.getAvgPriceForFaction(Faction.ALLIANCE);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getAvgPriceForFaction_whenFactionIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> serverPriceServiceImpl.getAvgPriceForFaction(null));
    }

    @Test
    void getAvgPriceForFaction_whenNoPricesFoundForFaction_throwsServerPriceNotFoundException() {
        when(serverPriceRepository.findAvgPriceByFaction(Faction.ALLIANCE, serverPriceServiceImpl.getAVERAGE_TIME_PERIOD()))
                .thenReturn(Optional.empty());
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getAvgPriceForFaction(Faction.ALLIANCE));
    }

    @Test
    void getAllPricesForServer_whenValidServerId_returnsListOfPriceResponses() {
        BigDecimal price = BigDecimal.valueOf(200);
        ServerPrice serverPrice = ServerPrice.builder()
                .value(price)
                .build();
        ServerPriceResponse expectedResult = ServerPriceResponse.builder()
                .currency(Currency.USD)
                .value(price)
                .build();

        when(serverPriceRepository.findAllPricesByServerId(1, pageable))
                .thenReturn(new PageImpl<>(List.of(serverPrice)));
        when(serverPriceMapper.toPriceResponse(serverPrice)).thenReturn(expectedResult);
        List<ServerPriceResponse> actualResult = serverPriceServiceImpl.getAllPricesForServer(1, pageable);

        assertThat(actualResult).containsExactlyInAnyOrder(expectedResult)
                .extracting(ServerPriceResponse::value)
                .containsExactlyInAnyOrder(expectedResult.value());
    }

    @Test
    void getAllPricesForServer_whenServerIdInvalid_throwsIllegalArgumentException() {
        int negativeServerId = -1;
        int zeroServerId = 0;
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAllPricesForServer(negativeServerId, pageable));
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAllPricesForServer(zeroServerId, pageable));
    }

    @Test
    void getAllPricesForServer_whenNoPricesFoundForServer_throwsServerPriceNotFoundException() {
        when(serverPriceRepository.findAllPricesByServerId(1, pageable)).thenReturn(new PageImpl<>(List.of()));
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getAllPricesForServer(1, pageable));
    }

    @Test
    void getAllPricesForServer_whenServerIdValidAndPageableIsNull_throwsNullPointerException() {
        int serverId = 1;
        assertThrows(NullPointerException.class, () -> serverPriceServiceImpl.getAllPricesForServer(serverId, null));
    }
    @Test
    void getAvgPriceForServer_whenServerNameValid_returnsAvgPriceForServer() {
        String serverName = "everlook-alliance";
        BigDecimal price = BigDecimal.valueOf(200).setScale(5, RoundingMode.HALF_UP);
        ServerPriceResponse expectedResult = ServerPriceResponse.builder()
                .currency(Currency.USD)
                .value(price)
                .build();

        when(serverPriceRepository.findAvgPriceByUniqueServerName(serverName, serverPriceServiceImpl.getAVERAGE_TIME_PERIOD()))
                .thenReturn(Optional.of(price));
        ServerPriceResponse actualResult = serverPriceServiceImpl.getAvgPriceForServer(serverName);

        assertThat(actualResult).isEqualTo(expectedResult);
    }
    @Test
    void getAvgPriceForServer_whenServerNameInvalid_throwsIllegalArgumentException() {
        String blankServerName = " ";
        String emptyServerName = "";
        String nullServerName = null;
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAvgPriceForServer(blankServerName));
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAvgPriceForServer(emptyServerName));
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAvgPriceForServer(nullServerName));
    }
    @Test
    void getAvgPriceForServer_whenNoPricesFoundForServerName_throwsServerPriceNotFoundException() {
        String serverName = "everlook-alliance";
        when(serverPriceRepository.findAvgPriceByUniqueServerName(serverName, serverPriceServiceImpl.getAVERAGE_TIME_PERIOD()))
                .thenReturn(Optional.empty());
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getAvgPriceForServer(serverName));
    }
    @Test
    void getAvgPriceForServer_whenServerIdValid_returnsAvgPriceForServer() {
        int serverId = 1;
        BigDecimal price = BigDecimal.valueOf(200).setScale(5, RoundingMode.HALF_UP);
        ServerPriceResponse expectedResult = ServerPriceResponse.builder()
                .currency(Currency.USD)
                .value(price)
                .build();

        when(serverPriceRepository.findAvgPriceByServerId(serverId, serverPriceServiceImpl.getAVERAGE_TIME_PERIOD())).thenReturn(Optional.of(price));
        ServerPriceResponse actualResult = serverPriceServiceImpl.getAvgPriceForServer(1);

        assertThat(actualResult).isEqualTo(expectedResult);
    }
    @Test
    void getAvgPriceForServer_whenServerIdInvalid_throwsIllegalArgumentException() {
        int negativeServerId = -1;
        int zeroServerId = 0;
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAvgPriceForServer(negativeServerId));
        assertThrows(IllegalArgumentException.class, () -> serverPriceServiceImpl.getAvgPriceForServer(zeroServerId));
    }
    @Test
    void getAvgPriceForServer_whenNoPricesFoundForServerId_throwsServerPriceNotFoundException() {
        int serverId = 1;
        when(serverPriceRepository.findAvgPriceByServerId(serverId, serverPriceServiceImpl.getAVERAGE_TIME_PERIOD()))
                .thenReturn(Optional.empty());
        assertThrows(ServerPriceNotFoundException.class, () -> serverPriceServiceImpl.getAvgPriceForServer(serverId));
    }

}