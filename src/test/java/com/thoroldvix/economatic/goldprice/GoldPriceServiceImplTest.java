package com.thoroldvix.economatic.goldprice;

import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.Server;
import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.shared.PaginationInfo;
import com.thoroldvix.economatic.shared.SearchCriteria;
import com.thoroldvix.economatic.shared.SearchRequest;
import com.thoroldvix.economatic.shared.TimeRange;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class GoldPriceServiceImplTest {
    public static final String SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY = "Server identifier cannot be null or empty";
    private static final LocalDateTime UPDATE_DATE = LocalDateTime.now();
    public static final String PAGEABLE_CANNOT_BE_NULL = "Pageable cannot be null";
    private static List<GoldPriceResponse> priceResponses;
    private static List<GoldPrice> prices;
    private static PageImpl<GoldPrice> page;
    private static PageRequest pageRequest;
    private static TimeRange timeRange;

    private static SearchRequest searchRequest;
    @Mock
    private ServerService serverService;
    @Mock
    private GoldPriceRepository goldPriceRepository;
    @Mock
    private GoldPriceMapper goldPriceMapper;

    @InjectMocks
    private GoldPriceServiceImpl goldPriceServiceImpl;

    private static GoldPriceListResponse buildGoldPriceListResponse(List<GoldPriceResponse> prices) {
        return GoldPriceListResponse.builder()
                .prices(prices)
                .build();
    }

    private static Server createServer(int id, Region region, Faction faction) {
        return Server.builder()
                .id(id)
                .region(region)
                .faction(faction)
                .build();
    }

    private static SearchRequest getSearchRequest() {
        SearchCriteria criteria = SearchCriteria.builder()
                .column("test")
                .operation(SearchCriteria.Operation.EQUALS)
                .joinTable("test-table")
                .value("123")
                .build();

        return SearchRequest.builder()
                .searchCriteria(Collections.singletonList(criteria))
                .globalOperator(SearchRequest.GlobalOperator.AND)
                .build();
    }

    @NotNull
    private static PageImpl<GoldPrice> getEmptyPage() {
        return new PageImpl<>(Collections.emptyList());
    }

    private static ServerResponse getServerResponse() {
        return ServerResponse.builder()
                .id(1)
                .build();
    }

    @BeforeAll
    static void setUp() {
        Server server1 = createServer(1, Region.EU, Faction.ALLIANCE);
        Server server2 = createServer(2, Region.US, Faction.HORDE);


        GoldPrice goldPrice1 = new GoldPrice(1L, BigDecimal.valueOf(0.1), UPDATE_DATE, server1);
        GoldPrice goldPrice2 = new GoldPrice(2L, BigDecimal.valueOf(0.2), UPDATE_DATE, server2);

        pageRequest = PageRequest.of(0, 100);

        GoldPriceResponse goldPriceResponse1 = convertToResponse(goldPrice1);
        GoldPriceResponse goldPriceResponse2 = convertToResponse(goldPrice2);

        timeRange = new TimeRange(7);
        searchRequest = getSearchRequest();
        priceResponses = List.of(goldPriceResponse1, goldPriceResponse2);
        prices = List.of(goldPrice1, goldPrice2);
        page = new PageImpl<>(List.of(goldPrice1, goldPrice2));
    }

    private static GoldPriceResponse convertToResponse(GoldPrice price) {
        String server = price.getServer().getUniqueName();
        LocalDateTime updatedAt = price.getUpdatedAt();
        BigDecimal value = price.getValue();

        return new GoldPriceResponse(value, server, updatedAt);
    }

    @Test
    void getAll_returnsCorrectGoldPricePageResponse() {
        GoldPricePageResponse expected = buildGoldPricePageResponse(priceResponses, page);

        when(goldPriceMapper.toPageResponse(any())).thenReturn(expected);
        when(goldPriceRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageRequest)).thenReturn(page);

        GoldPricePageResponse actual = goldPriceServiceImpl.getAll(timeRange, pageRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAll_throwsNullPointerException_whenTimeRangeIsNull() {
        assertThatThrownBy(() -> goldPriceServiceImpl.getAll(null, pageRequest))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Time range cannot be null");
    }

    @Test
    void getAll_throwsNullPointerException_whenPageableIsNull() {
        assertThatThrownBy(() -> goldPriceServiceImpl.getAll(timeRange, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Pageable cannot be null");
    }

    @Test
    void getAll_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPricePage() {
        when(goldPriceRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageRequest))
                .thenReturn(getEmptyPage());

        assertThatThrownBy(() -> goldPriceServiceImpl.getAll(timeRange, pageRequest))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found for time range: %s-%s".formatted(timeRange.start(), timeRange.end()));
    }

    @Test
    void getAllRecent_returnsCorrectGoldPriceListResponse() {
        GoldPriceListResponse expected = buildGoldPriceListResponse(priceResponses);

        when(goldPriceMapper.toGoldPriceList(prices)).thenReturn(expected);
        when(goldPriceRepository.findAllRecent()).thenReturn(prices);

        GoldPriceListResponse actual = goldPriceServiceImpl.getAllRecent();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllRecent_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPriceList() {
        when(goldPriceRepository.findAllRecent()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> goldPriceServiceImpl.getAllRecent())
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found");
    }

    @Test
    void search_returnsCorrectGoldPricePageResponse() {
        GoldPricePageResponse expected = buildGoldPricePageResponse(priceResponses, page);

        when(goldPriceMapper.toPageResponse(page)).thenReturn(expected);
        when(goldPriceRepository.findAll(ArgumentMatchers.<Specification<GoldPrice>>any(), eq(pageRequest))).thenReturn(page);

        GoldPricePageResponse actual = goldPriceServiceImpl.search(searchRequest, pageRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void search_throwsNullPointerException_whenPageableIsNull() {
        assertThatThrownBy(() -> goldPriceServiceImpl.search(searchRequest, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(PAGEABLE_CANNOT_BE_NULL);
    }

    @Test
    void search_throwsNullPointerException_whenSearchRequestIsNull() {
        assertThatThrownBy(() -> goldPriceServiceImpl.search(null, pageRequest))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Search request cannot be null");
    }

    @Test
    void search_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPricePage() {
        when(goldPriceRepository.findAll(ArgumentMatchers.<Specification<GoldPrice>>any(), any(Pageable.class)))
                .thenReturn(getEmptyPage());

        assertThatThrownBy(() -> goldPriceServiceImpl.search(searchRequest, pageRequest))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found");
    }

    @Test
    void getForServer_returnsCorrectGoldPricePageResponse() {
        String serverIdentifier = "123";
        ServerResponse server = ServerResponse.builder()
                .id(123)
                .build();

        GoldPricePageResponse expected = buildGoldPricePageResponse(priceResponses, page);

        when(goldPriceMapper.toPageResponse(page)).thenReturn(expected);
        when(serverService.getServer(serverIdentifier)).thenReturn(server);
        when(goldPriceRepository.findAllForServerAndTimeRange(server.id(), timeRange.start(), timeRange.end(), pageRequest)).thenReturn(page);

        GoldPricePageResponse actual = goldPriceServiceImpl.getForServer(serverIdentifier, timeRange, pageRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getForServer_throwsIllegalArgumentException_whenServeIdentifierIsInvalid(String serverIdentifier) {
        assertThatThrownBy(() -> goldPriceServiceImpl.getForServer(serverIdentifier, timeRange, pageRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
    }

    @Test
    void getForServer_throwsNullPointerException_whenTimeRangeIsNull() {
        String serverIdentifier = "123";

        assertThatThrownBy(() -> goldPriceServiceImpl.getForServer(serverIdentifier, null, pageRequest))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Time range cannot be null");
    }

    @Test
    void getForServer_throwsNullPointerException_whenPageableIsNull() {
        TimeRange timeRange = new TimeRange(7);
        String serverIdentifier = "123";

        assertThatThrownBy(() -> goldPriceServiceImpl.getForServer(serverIdentifier, timeRange, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(PAGEABLE_CANNOT_BE_NULL);
    }

    @Test
    void getForServer_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPricePage() {
        String serverIdentifier = "123";
        ServerResponse server = ServerResponse.builder()
                .id(123)
                .build();

        when(serverService.getServer(serverIdentifier)).thenReturn(server);
        when(goldPriceRepository.findAllForServerAndTimeRange(server.id(), timeRange.start(), timeRange.end(), pageRequest))
                .thenReturn(getEmptyPage());

        assertThatThrownBy(() -> goldPriceServiceImpl.getForServer(serverIdentifier, timeRange, pageRequest))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found for server identifier %s and time range: %s-%s".formatted(
                        serverIdentifier, timeRange.start(), timeRange.end()));
    }

    @Test
    void getRecentForServer_returnsCorrectGoldPriceResponse() {
        String serverIdentifier = "1";
        ServerResponse server = getServerResponse();
        GoldPrice goldPrice1 = prices.get(0);
        GoldPriceResponse goldPriceResponse1 = priceResponses.get(0);

        when(goldPriceMapper.toResponse(goldPrice1)).thenReturn(goldPriceResponse1);
        when(serverService.getServer(serverIdentifier)).thenReturn(server);
        when(goldPriceRepository.findRecentForServer(server.id())).thenReturn(Optional.ofNullable(goldPrice1));

        GoldPriceResponse actual = goldPriceServiceImpl.getRecentForServer(serverIdentifier);

        assertThat(actual).isEqualTo(goldPriceResponse1);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getRecentForServer_throwsIllegalArgumentException_whenServerIdentifierIsInvalid(String serverIdentifier) {
        assertThatThrownBy(() -> goldPriceServiceImpl.getRecentForServer(serverIdentifier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
    }

    @Test
    void getRecentForServer_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyOptional() {
        String serverIdentifier = "1";
        ServerResponse server = getServerResponse();

        when(serverService.getServer(serverIdentifier)).thenReturn(server);
        when(goldPriceRepository.findRecentForServer(server.id())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goldPriceServiceImpl.getRecentForServer(serverIdentifier))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found for server " + serverIdentifier);
    }

    @Test
    void getRecentForRegion_returnsCorrectGoldPriceListResponse_whenEuRegion() {
        checkGetRecentForRegion("eu", Region.EU);
    }

    @Test
    void getRecentForRegion_returnsCorrectGoldPriceListResponse_whenUsRegion() {
        checkGetRecentForRegion("us", Region.US);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getRecentForRegion_throwsIllegalArgumentException_whenRegionNameIsInvalid(String regionName) {
        assertThatThrownBy(() -> goldPriceServiceImpl.getRecentForRegion(regionName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Region name cannot be null or empty");
    }

    @Test
    void getRecentForRegion_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPriceList() {
        String regionName = "eu";
        Region region = Region.EU;
        when(goldPriceRepository.findRecentForRegion(region.ordinal())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> goldPriceServiceImpl.getRecentForRegion(regionName))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found for region " + regionName);
    }

    @Test
    void getRecentForFaction_returnsCorrectGoldPriceListResponse_whenAllianceFaction() {
        checkGetRecentForFaction("Alliance", Faction.ALLIANCE);
    }

    @Test
    void getRecentForRegion_returnsCorrectGoldPriceListResponse_whenHordeFaction() {
        checkGetRecentForFaction("Horde", Faction.HORDE);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getRecentForFaction_throwsIllegalArgumentException_whenFactionNameIsInvalid(String factionName) {
        assertThatThrownBy(() -> goldPriceServiceImpl.getRecentForFaction(factionName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Faction name cannot be null or empty");
    }

    @Test
    void getRecentForFaction_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPriceList() {
        String factionName = "Alliance";
        Faction faction = Faction.ALLIANCE;
        when(goldPriceRepository.findRecentForFaction(faction.ordinal())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> goldPriceServiceImpl.getRecentForFaction(factionName))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found for faction " + factionName);
    }

    @Test
    void getRecentForServerList_returnsCorrectGoldPriceListResponse() {
        Set<Integer> serverIds = Set.of(1, 2);
        String server1Id = "1";
        String server2Id = "2";
        GoldPriceRequest request = GoldPriceRequest.builder()
                .serverList(Set.of(server1Id, server2Id))
                .build();
        ServerResponse server1 = ServerResponse.builder().id(1).build();
        ServerResponse server2 = ServerResponse.builder().id(2).build();
        GoldPriceListResponse expected = buildGoldPriceListResponse(priceResponses);

        when(goldPriceMapper.toGoldPriceList(prices)).thenReturn(expected);
        when(serverService.getServer(server1Id)).thenReturn(server1);
        when(serverService.getServer(server2Id)).thenReturn(server2);
        when(goldPriceRepository.findRecentForServers(serverIds)).thenReturn(prices);

        GoldPriceListResponse actual = goldPriceServiceImpl.getRecentForServerList(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRecentForServerList_throwsNullPointerException_whenGoldPriceRequestIsNull() {
        assertThatThrownBy(() -> goldPriceServiceImpl.getRecentForServerList(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Gold price request cannot be null");
    }

    @Test
    void getRecentForServerList_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPriceList() {
        ServerResponse server = ServerResponse.builder().id(1).build();
        String server1Id = "1";
        GoldPriceRequest request = GoldPriceRequest.builder()
                .serverList(Set.of(server1Id))
                .build();
        when(serverService.getServer(server1Id)).thenReturn(server);
        when(goldPriceRepository.findRecentForServers(Set.of(1))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> goldPriceServiceImpl.getRecentForServerList(request))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found for server list");
    }

    @Test
    void saveAll_savesPricesToDB() {
        goldPriceServiceImpl.saveAll(prices);
        verify(goldPriceRepository, times(1)).saveAll(prices);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void saveAll_throwsIllegalArgumentException_whenGoldPriceListIsInvalid(List<GoldPrice> prices) {
        assertThatThrownBy(() -> goldPriceServiceImpl.saveAll(prices))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Prices cannot be null or empty");

    }

    private GoldPricePageResponse buildGoldPricePageResponse(List<GoldPriceResponse> prices, Page<GoldPrice> page) {
        PaginationInfo paginationInfo = new PaginationInfo(page);
        return GoldPricePageResponse.builder()
                .prices(prices)
                .paginationInfo(paginationInfo)
                .build();
    }

    private void checkGetRecentForRegion(String regionName, Region region) {
        GoldPriceListResponse expected = buildGoldPriceListResponse(priceResponses);

        when(goldPriceMapper.toGoldPriceList(prices)).thenReturn(expected);
        when(goldPriceRepository.findRecentForRegion(region.ordinal())).thenReturn(prices);

        GoldPriceListResponse actual = goldPriceServiceImpl.getRecentForRegion(regionName);

        assertThat(actual).isEqualTo(expected);
    }

    private void checkGetRecentForFaction(String factionName, Faction faction) {
        GoldPriceListResponse expected = buildGoldPriceListResponse(priceResponses);

        when(goldPriceMapper.toGoldPriceList(prices)).thenReturn(expected);
        when(goldPriceRepository.findRecentForFaction(faction.ordinal())).thenReturn(prices);

        GoldPriceListResponse actual = goldPriceServiceImpl.getRecentForFaction(factionName);

        assertThat(actual).isEqualTo(expected);
    }

}