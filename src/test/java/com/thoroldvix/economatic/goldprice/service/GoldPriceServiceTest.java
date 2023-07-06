package com.thoroldvix.economatic.goldprice.service;

import com.thoroldvix.economatic.error.ErrorMessages;
import com.thoroldvix.economatic.goldprice.GoldPriceSetupBaseTest;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceListResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPricePageResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.error.GoldPriceNotFoundException;
import com.thoroldvix.economatic.goldprice.mapper.GoldPriceMapper;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.goldprice.repository.GoldPriceRepository;
import com.thoroldvix.economatic.server.service.ServerService;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import com.thoroldvix.economatic.shared.dto.TimeRange;
import com.thoroldvix.economatic.shared.service.SearchSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class GoldPriceServiceTest extends GoldPriceSetupBaseTest {
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

    private TimeRange timeRange;
    private PageRequest pageRequest;
    private GoldPriceResponse goldPriceResponse1;
    private GoldPriceResponse goldPriceResponse2;
    private Page<GoldPrice> page;

    private static GoldPriceListResponse buildGoldPriceListResponse(List<GoldPriceResponse> prices) {
        return GoldPriceListResponse.builder()
                .prices(prices)
                .build();
    }

    @BeforeEach
    void setUp() {
        timeRange = new TimeRange(7);
        pageRequest = PageRequest.of(0, 100);
        goldPriceResponse1 = convertToResponse(goldPrice1);
        goldPriceResponse2 = convertToResponse(goldPrice2);
        page = new PageImpl<>(List.of(goldPrice1, goldPrice2));
    }

    @Test
    void getAll_returnsCorrectGoldPricePageResponse() {
        List<GoldPriceResponse> prices = List.of(goldPriceResponse1, goldPriceResponse2);
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
                .hasMessage(ErrorMessages.TIME_RANGE_CANNOT_BE_NULL);
    }

    @Test
    void getAll_throwsNullPointerException_whenPageableIsNull() {
        assertThatThrownBy(() -> goldPriceService.getAll(timeRange, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(ErrorMessages.PAGEABLE_CANNOT_BE_NULL);
    }

    @Test
    void getAll_throwsGoldPriceNotFoundException_whenRepositoryReturnsEmptyGoldPricePage() {
        when(goldPriceRepository.findAllForTimeRange(timeRange.start(), timeRange.end(), pageRequest))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

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