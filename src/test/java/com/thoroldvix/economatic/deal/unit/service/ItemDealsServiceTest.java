package com.thoroldvix.economatic.deal.unit.service;

import com.thoroldvix.economatic.deal.BaseItemDealTest;
import com.thoroldvix.economatic.deal.dto.ItemDealsList;
import com.thoroldvix.economatic.deal.mapper.ItemDealsMapper;
import com.thoroldvix.economatic.deal.repository.ItemDealProjection;
import com.thoroldvix.economatic.deal.repository.ItemDealsRepository;
import com.thoroldvix.economatic.deal.service.ItemDealsService;
import com.thoroldvix.economatic.server.dto.ServerResponse;
import com.thoroldvix.economatic.server.service.ServerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


class ItemDealsServiceTest extends BaseItemDealTest {
    @Mock
    private ServerService serverService;
    @Mock
    private ItemDealsRepository itemDealsRepository;
    @Mock
    private ItemDealsMapper itemDealsMapper;
    @InjectMocks
    private ItemDealsService itemDealsService;
    private ItemDealsList itemDealsList;
    private ServerResponse server;
    private List<ItemDealProjection> dealProjectionList;


    private static ServerResponse getServer() {
        return ServerResponse.builder()
                .id(SERVER_ID)
                .uniqueName(SERVER_NAME)
                .build();
    }

    @BeforeEach
    void setup() {
        itemDealsList =  getItemDealsList();
        server = getServer();
        dealProjectionList = List.of(getDealProjection());
    }

    @Test
    void getDealsForServer_returnsCorrectItemDealList_whenValidInputsProvided() {
        setupMocking();
        ItemDealsList actualResponse = itemDealsService.getDealsForServer(SERVER_NAME, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT);
        assertThat(actualResponse).isEqualTo(itemDealsList);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getDealsForServer_throwsIllegalArgumentException_whenServerIdentifierIsInvalid(String serverIdentifier) {
        assertThatThrownBy(() -> itemDealsService.getDealsForServer(serverIdentifier, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getDealsForServer_throwsIllegalArgumentException_whenItemQuantityLessThanMin() {
        assertThatThrownBy(() -> itemDealsService.getDealsForServer(SERVER_NAME, 0, MINIMUM_ITEM_QUALITY, ITEM_LIMIT))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @ParameterizedTest
    @ValueSource(ints = {-1, 6})
    void getDealsForServer_throwsIllegalArgumentException_whenItemQualityInvalid(int quality) {
        assertThatThrownBy(() -> itemDealsService.getDealsForServer(SERVER_NAME, MINIMUM_ITEM_QUANTITY, quality, ITEM_LIMIT))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void getDealsForServer_throwsIllegalArgumentException_whenItemLimitLessThanMin() {
        assertThatThrownBy(() -> itemDealsService.getDealsForServer(SERVER_NAME, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void setupMocking() {
        when(serverService.getServer(SERVER_NAME)).thenReturn(server);
        when(itemDealsRepository.findDealsForServer(SERVER_ID, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT))
                .thenReturn(dealProjectionList);
        when(itemDealsMapper.toItemDealsList(dealProjectionList)).thenReturn(itemDealsList);
    }
}