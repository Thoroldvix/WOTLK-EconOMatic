package com.thoroldvix.pricepal.server.service;

import com.thoroldvix.pricepal.server.dto.ServerPriceResponse;
import com.thoroldvix.pricepal.server.entity.Server;
import com.thoroldvix.pricepal.server.entity.ServerPrice;
import com.thoroldvix.pricepal.server.error.ServerPriceNotFoundException;
import com.thoroldvix.pricepal.server.repository.ServerPriceRepository;
import org.junit.internal.requests.FilterRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public
class ServerPriceServiceTest {

    private static List<ServerPriceResponse> priceResponses;
    private static List<ServerPrice> prices;
    private static FilterRequest filters;
    private final Pageable unpaged = Pageable.unpaged();
    @Mock
    private ServerPriceRepository serverPriceRepository;
    @Mock
    private ServerService serverService;
    @Mock
    private ServerPriceMapper serverPriceMapper;
    @InjectMocks
    private ServerPriceService serverPriceService;

    @BeforeAll
    public static void setUp() {
        BigDecimal price1 = BigDecimal.TEN;
        BigDecimal price2 = BigDecimal.valueOf(30);
        BigDecimal price3 = BigDecimal.valueOf(40);
        BigDecimal price4 = BigDecimal.valueOf(20);
        Currency currency = Currency.USD;
        filters = FilterRequest.builder().build();
        priceResponses = new ArrayList<>();
        prices = new ArrayList<>();

        for (var price : List.of(price1, price2, price3, price4)) {
            priceResponses.add(getServerPriceResponse(price, currency));
            prices.add(getServerPrice(price, currency));
        }
    }

    public static ServerPriceResponse getServerPriceResponse(BigDecimal price, Currency currency) {
        return ServerPriceResponse.builder()
                .price(price.setScale(5, RoundingMode.HALF_UP))
                .currency(currency)
                .build();
    }

    private static ServerPrice getServerPrice(BigDecimal price, Currency currency) {
        Server server = Server.builder()
                .id(1)
                .serverPrices(new ArrayList<>())
                .build();
        ServerPrice serverPrice = ServerPrice.builder()
                .price(price.setScale(5, RoundingMode.HALF_UP))
                .currency(currency)
                .build();
        serverPrice.setServer(server);
        return serverPrice;
    }

    private void stubGetAllPrices() {
        when(serverPriceRepository.findAll(ArgumentMatchers.<Specification<ServerPrice>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(prices));
        when(serverPriceMapper.toPriceResponseList(prices)).thenReturn(priceResponses);
    }

    @Test
    void getFilteredPrices_whenFiltersAndPageableProvided_returnFilteredPrices() {
        stubGetAllPrices();
        List<ServerPriceResponse> actualResponse = serverPriceService.searchForPrices(filters, unpaged);
        assertThat(actualResponse).isEqualTo(priceResponses);
    }




    @Test
    void getFilteredPrices_whenPageableIsNull_throwsNullPointerException() {
        assertThatThrownBy(() -> serverPriceService.searchForPrices(filters, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getFilteredPrices_whenNoPricesFoundForFilters_throwsServerPriceNotFoundException() {
        when(serverPriceRepository.findAll(ArgumentMatchers.<Specification<ServerPrice>>any(),
                any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        assertThatThrownBy(() -> serverPriceService.searchForPrices(filters, unpaged))
                .isInstanceOf(ServerPriceNotFoundException.class);
    }

    @Test
    void getAvgPrices_whenFiltersProvided_returnAvgPriceResponse() {
        BigDecimal price = BigDecimal.valueOf(25);
        stubGetAllPrices();
        ServerPriceResponse avgPrice = getServerPriceResponse(price, Currency.USD);
        ServerPriceResponse actualResult = serverPriceService.getAvgPrices(filters);


        assertThat(actualResult).isEqualTo(avgPrice);
    }




    @Test
    void getMedianPrices_whenFiltersProvided_returnMedianPriceResponse() {
        stubGetAllPrices();
        ServerPriceResponse medianPrice = getServerPriceResponse(BigDecimal.valueOf(25), Currency.USD);
        ServerPriceResponse actualResult = serverPriceService.getMedianPrices(filters);
        assertThat(actualResult).isEqualTo(medianPrice);
    }




    @Test
    void getMaxPrices_whenFiltersProvided_returnMaxPriceResponse() {
        stubGetAllPrices();
        ServerPriceResponse maxPrice = getServerPriceResponse(BigDecimal.valueOf(40), Currency.USD);
        ServerPriceResponse actualResult = serverPriceService.getMaxPrices(filters);
        assertThat(actualResult).isEqualTo(maxPrice);
    }




    @Test
    void getMinPrices_whenFiltersProvided_returnMinPriceResponse() {
        stubGetAllPrices();
        ServerPriceResponse minPrice = getServerPriceResponse(BigDecimal.TEN, Currency.USD);
        ServerPriceResponse actualResult = serverPriceService.getMinPrices(filters);
        assertThat(actualResult).isEqualTo(minPrice);
    }


    @Test
    void savePrice_whenServerIdAndPriceProvided_savesPriceInDb() {
        ServerPrice serverPrice = prices.get(0);
        ServerPriceResponse priceResponse = priceResponses.get(0);
        Server server = serverPrice.getServer();
        when(serverService.getServer(server.getId())).thenReturn(server);
        when(serverPriceMapper.toServerPrice(priceResponse)).thenReturn(serverPrice);
        serverPriceService.savePrice(server.getId(), priceResponse);
        verify(serverPriceRepository).save(serverPrice);
    }

    @Test
    void savePrice_whenServerIdInvalid_throwsIllegalArgumentException() {
        ServerPriceResponse priceResponse = priceResponses.get(0);
        when(serverService.getServer(0)).thenThrow(IllegalArgumentException.class);
        assertThatThrownBy(() -> serverPriceService.savePrice(0, priceResponse))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void savePrice_whenRecentPriceNull_throwsNullPointerException() {
        assertThatThrownBy(() -> serverPriceService.savePrice(1, null))
                .isInstanceOf(NullPointerException.class);
    }
}