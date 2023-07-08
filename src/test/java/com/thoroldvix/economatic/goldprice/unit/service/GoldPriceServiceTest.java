package com.thoroldvix.economatic.goldprice.unit.service;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceListResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPricePageResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.error.GoldPriceNotFoundException;
import com.thoroldvix.economatic.goldprice.mapper.GoldPriceMapper;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.goldprice.repository.GoldPriceRepository;
import com.thoroldvix.economatic.goldprice.service.GoldPriceService;
import com.thoroldvix.economatic.server.dto.ServerResponse;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.model.Server;
import com.thoroldvix.economatic.server.service.ServerService;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import com.thoroldvix.economatic.shared.dto.SearchCriteria;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import com.thoroldvix.economatic.shared.dto.TimeRange;
import com.thoroldvix.economatic.shared.service.SearchSpecification;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class GoldPriceServiceTest {
    public static final String SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY = "Server identifier cannot be null or empty";
    private final LocalDateTime UPDATE_DATE = LocalDateTime.now();

    @Mock
    private ServerService serverService;
    @Mock
    private GoldPriceRepository goldPriceRepository;

    @Mock
    private GoldPriceMapper goldPriceMapper;
    @Mock
    private SearchSpecification<GoldPrice> searchSpecification;

    @InjectMocks
    private GoldPriceService goldPriceService;

    private PageRequest pageRequest;
    private GoldPriceResponse goldPriceResponse1;
    private GoldPriceResponse goldPriceResponse2;
    private GoldPrice goldPrice1;
    private GoldPrice goldPrice2;


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
                .searchCriteria(new SearchCriteria[]{criteria})
                .globalOperator(SearchRequest.GlobalOperator.AND)
                .build();
    }

    @NotNull
    private static PageImpl<GoldPrice> getEmptyPage() {
        return new PageImpl<>(Collections.emptyList());
    }

    @BeforeEach
    void setUp() {
        Server server1 = createServer(1, Region.EU, Faction.ALLIANCE);
        Server server2 = createServer(2, Region.US, Faction.HORDE);


        goldPrice1 = new GoldPrice(1L, BigDecimal.valueOf(0.1), UPDATE_DATE, server1);
        goldPrice2 = new GoldPrice(2L, BigDecimal.valueOf(0.2), UPDATE_DATE, server2);

        pageRequest = PageRequest.of(0, 100);

        goldPriceResponse1 = convertToResponse(goldPrice1);
        goldPriceResponse2 = convertToResponse(goldPrice2);
    }

    @Test
    void getAll_returnsCorrectGoldPricePageResponse() {
        TimeRange timeRange = new TimeRange(7);
        List<GoldPriceResponse> prices = List.of(goldPriceResponse1, goldPriceResponse2);
        PageImpl<GoldPrice> page = new PageImpl<>(List.of(goldPrice1, goldPrice2));
        GoldPricePageResponse expected = buildGoldPricePageResponse(prices, page);

        when(goldPriceMapper.toPageResponse(any())).thenReturn(expected);
        when(goldPriceRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageRequest)).thenReturn(page);

        GoldPricePageResponse actual = goldPriceService.getAll(timeRange, pageRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAll_throwsNullPointerException_whenTimeRangeIsNull() {
        assertThatThrownBy(() -> goldPriceService.getAll(null, pageRequest))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Time range cannot be null");
    }

    @Test
    void getAll_throwsNullPointerException_whenPageableIsNull() {
        TimeRange timeRange = new TimeRange(7);

        assertThatThrownBy(() -> goldPriceService.getAll(timeRange, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Pageable cannot be null");
    }

    @Test
    void getAll_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPricePage() {
        TimeRange timeRange = new TimeRange(7);

        when(goldPriceRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageRequest))
                .thenReturn(getEmptyPage());

        assertThatThrownBy(() -> goldPriceService.getAll(timeRange, pageRequest))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found for time range: %s-%s".formatted(timeRange.start(), timeRange.end()));
    }

    @Test
    void getAllRecent_returnsCorrectGoldPriceListResponse() {
        List<GoldPriceResponse> priceResponses = List.of(goldPriceResponse1, goldPriceResponse2);
        List<GoldPrice> prices = List.of(goldPrice1, goldPrice2);
        GoldPriceListResponse expected = buildGoldPriceListResponse(priceResponses);

        when(goldPriceMapper.toGoldPriceList(prices)).thenReturn(expected);
        when(goldPriceRepository.findAllRecent()).thenReturn(prices);

        GoldPriceListResponse actual = goldPriceService.getAllRecent();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllRecent_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPriceList() {
        when(goldPriceRepository.findAllRecent()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> goldPriceService.getAllRecent())
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found");
    }

    @Test
    void search_returnsCorrectGoldPricePageResponse() {
        SearchRequest searchRequest = getSearchRequest();
        List<GoldPriceResponse> prices = List.of(goldPriceResponse1, goldPriceResponse2);
        PageImpl<GoldPrice> page = new PageImpl<>(List.of(goldPrice1, goldPrice2));
        Specification<GoldPrice> specification = Specification.where(null);
        GoldPricePageResponse expected = buildGoldPricePageResponse(prices, page);

        when(goldPriceMapper.toPageResponse(page)).thenReturn(expected);
        when(searchSpecification.create(searchRequest.globalOperator(), searchRequest.searchCriteria())).thenReturn(specification);
        when(goldPriceRepository.findAll(specification, pageRequest)).thenReturn(page);

        GoldPricePageResponse actual = goldPriceService.search(searchRequest, pageRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void search_throwsNullPointerException_whenPageableIsNull() {
        SearchRequest searchRequest = getSearchRequest();

        assertThatThrownBy(() -> goldPriceService.search(searchRequest, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Pageable cannot be null");
    }

    @Test
    void search_throwsNullPointerException_whenSearchRequestIsNull() {
        assertThatThrownBy(() -> goldPriceService.search(null, pageRequest))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Search request cannot be null");
    }

    @Test
    void search_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPricePage() {
        SearchRequest searchRequest = getSearchRequest();

        when(goldPriceRepository.findAll(ArgumentMatchers.<Specification<GoldPrice>>any(), any(Pageable.class)))
                .thenReturn(getEmptyPage());

        assertThatThrownBy(() -> goldPriceService.search(searchRequest, pageRequest))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found");
    }

    @Test
    void getForServer_returnsCorrectGoldPricePageResponse() {
        String serverIdentifier = "123";
        ServerResponse server = ServerResponse.builder()
                .id(123)
                .build();

        TimeRange timeRange = new TimeRange(7);
        List<GoldPriceResponse> prices = List.of(goldPriceResponse1, goldPriceResponse2);
        PageImpl<GoldPrice> page = new PageImpl<>(List.of(goldPrice1, goldPrice2));
        GoldPricePageResponse expected = buildGoldPricePageResponse(prices, page);

        when(goldPriceMapper.toPageResponse(page)).thenReturn(expected);
        when(serverService.getServer(serverIdentifier)).thenReturn(server);
        when(goldPriceRepository.findAllForServerAndTimeRange(server.id(), timeRange.start(), timeRange.end(), pageRequest)).thenReturn(page);

        GoldPricePageResponse actual = goldPriceService.getForServer(serverIdentifier, timeRange, pageRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getForServer_throwsIllegalArgumentException_whenServeIdentifierIsInvalid(String serverIdentifier) {
        TimeRange timeRange = new TimeRange(7);

        assertThatThrownBy(() -> goldPriceService.getForServer(serverIdentifier, timeRange, pageRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
    }

    @Test
    void getForServer_throwsNullPointerException_whenTimeRangeIsNull() {
        String serverIdentifier = "123";

        assertThatThrownBy(() -> goldPriceService.getForServer(serverIdentifier, null, pageRequest))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Time range cannot be null");
    }

    @Test
    void getForServer_throwsNullPointerException_whenPageableIsNull() {
        TimeRange timeRange = new TimeRange(7);
        String serverIdentifier = "123";

        assertThatThrownBy(() -> goldPriceService.getForServer(serverIdentifier, timeRange, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Pageable cannot be null");
    }

    @Test
    void getForServer_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPricePage() {
        String serverIdentifier = "123";
        ServerResponse server = ServerResponse.builder()
                .id(123)
                .build();
        TimeRange timeRange = new TimeRange(7);

        when(serverService.getServer(serverIdentifier)).thenReturn(server);
        when(goldPriceRepository.findAllForServerAndTimeRange(server.id(), timeRange.start(), timeRange.end(), pageRequest))
                .thenReturn(getEmptyPage());

        assertThatThrownBy(() -> goldPriceService.getForServer(serverIdentifier, timeRange, pageRequest))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found for server identifier %s and time range: %s-%s".formatted(
                        serverIdentifier, timeRange.start(), timeRange.end()));
    }

    @Test
    void getRecentForServer_returnsCorrectGoldPriceResponse() {
        String serverIdentifier = "1";
        ServerResponse server = getServerResponse();

        when(goldPriceMapper.toResponse(goldPrice1)).thenReturn(goldPriceResponse1);
        when(serverService.getServer(serverIdentifier)).thenReturn(server);
        when(goldPriceRepository.findRecentForServer(server.id())).thenReturn(Optional.ofNullable(goldPrice1));

        GoldPriceResponse actual = goldPriceService.getRecentForServer(serverIdentifier);

        assertThat(actual).isEqualTo(goldPriceResponse1);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getRecentForServer_throwsIllegalArgumentException_whenServerIdentifierIsInvalid(String serverIdentifier) {
        assertThatThrownBy(() -> goldPriceService.getRecentForServer(serverIdentifier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
    }

    @Test
    void getRecentForServer_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyOptional() {
        String serverIdentifier = "1";
        ServerResponse server = getServerResponse();

        when(serverService.getServer(serverIdentifier)).thenReturn(server);
        when(goldPriceRepository.findRecentForServer(server.id())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goldPriceService.getRecentForServer(serverIdentifier))
                .isInstanceOf(GoldPriceNotFoundException.class)
                .hasMessage("No prices found for server " + serverIdentifier);
    }

    private static ServerResponse getServerResponse() {
        return ServerResponse.builder()
                .id(1)
                .build();
    }

    GoldPriceResponse convertToResponse(GoldPrice price) {
        String server = price.getServer().getUniqueName();
        LocalDateTime updatedAt = price.getUpdatedAt();
        BigDecimal value = price.getValue();

        return new GoldPriceResponse(value, server, updatedAt);
    }

    GoldPricePageResponse buildGoldPricePageResponse(List<GoldPriceResponse> prices, Page<GoldPrice> page) {
        PaginationInfo paginationInfo = new PaginationInfo(page);
        return GoldPricePageResponse.builder()
                .prices(prices)
                .paginationInfo(paginationInfo)
                .build();
    }
}