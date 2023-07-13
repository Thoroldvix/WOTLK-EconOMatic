package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.server.ServerService;
import com.thoroldvix.economatic.server.ServerResponse;
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


class ItemDealsServiceImplTest extends BaseItemDealTest {
    @Mock
    private ServerService serverService;
    @Mock
    private ItemDealsRepository itemDealsRepository;
    @Mock
    private ItemDealsMapper itemDealsMapper;
    @InjectMocks
    private ItemDealsServiceImpl itemDealsServiceImpl;
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
        ItemDealsList actualResponse = itemDealsServiceImpl.getDealsForServer(SERVER_NAME, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT);
        assertThat(actualResponse).isEqualTo(itemDealsList);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getDealsForServer_throwsIllegalArgumentException_whenServerIdentifierIsInvalid(String serverIdentifier) {
        assertThatThrownBy(() -> itemDealsServiceImpl.getDealsForServer(serverIdentifier, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getDealsForServer_throwsIllegalArgumentException_whenItemQuantityLessThanMin() {
        assertThatThrownBy(() -> itemDealsServiceImpl.getDealsForServer(SERVER_NAME, 0, MINIMUM_ITEM_QUALITY, ITEM_LIMIT))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @ParameterizedTest
    @ValueSource(ints = {-1, 6})
    void getDealsForServer_throwsIllegalArgumentException_whenItemQualityInvalid(int quality) {
        assertThatThrownBy(() -> itemDealsServiceImpl.getDealsForServer(SERVER_NAME, MINIMUM_ITEM_QUANTITY, quality, ITEM_LIMIT))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void getDealsForServer_throwsIllegalArgumentException_whenItemLimitLessThanMin() {
        assertThatThrownBy(() -> itemDealsServiceImpl.getDealsForServer(SERVER_NAME, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void setupMocking() {
        when(serverService.getServer(SERVER_NAME)).thenReturn(server);
        when(itemDealsRepository.findDealsForServer(SERVER_ID, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT))
                .thenReturn(dealProjectionList);
        when(itemDealsMapper.toItemDealsList(dealProjectionList)).thenReturn(itemDealsList);
    }
}