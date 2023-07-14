package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        itemDealsList = getItemDealsList();
        server = getServer();
        dealProjectionList = List.of(getDealProjection());
    }

    @Test
    void getDealsForServer_returnsCorrectItemDealList_whenValidInputsProvided() {
        ItemDealsRequest itemDealsRequest = new ItemDealsRequest(SERVER_NAME, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT);

        when(serverService.getServer(SERVER_NAME)).thenReturn(server);
        when(itemDealsRepository.findDealsForServer(SERVER_ID, MINIMUM_ITEM_QUANTITY, MINIMUM_ITEM_QUALITY, ITEM_LIMIT))
                .thenReturn(dealProjectionList);
        when(itemDealsMapper.toItemDealsList(dealProjectionList)).thenReturn(itemDealsList);


        ItemDealsList actualResponse = itemDealsServiceImpl.getDealsForServer(itemDealsRequest);
        assertThat(actualResponse).isEqualTo(itemDealsList);
    }

    @Test
    void getDealsForServer_throwsNullPointerException_ItemDealsRequestIsNull() {
        assertThatThrownBy(() -> itemDealsServiceImpl.getDealsForServer(null))
                .isInstanceOf(NullPointerException.class);
    }
}