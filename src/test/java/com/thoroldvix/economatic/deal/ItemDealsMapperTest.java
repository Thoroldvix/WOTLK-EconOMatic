package com.thoroldvix.economatic.deal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ItemDealsMapperTest extends BaseItemDealTest {

    private final ItemDealsMapper itemDealsMapper = Mappers.getMapper(ItemDealsMapper.class);
    private ItemDealResponse itemDealResponse;
    private ItemDealProjection itemDealProjection;

    @BeforeEach
    void setUp() {
        itemDealResponse = getItemDealResponse();
        itemDealProjection = getDealProjection();
    }

    @Test
    void toResponse_returnCorrectItemDealResponse_whenValidItemDealProjectionProvided() {
        ItemDealResponse actualDealResponse = itemDealsMapper.toResponse(itemDealProjection);
        assertThat(actualDealResponse).isEqualTo(itemDealResponse);
    }

    @Test
    void toResponseList_returnsCorrectItemDealResponseList_whenValidItemDealProjectionProvided() {
        List<ItemDealResponse> actualResponse = itemDealsMapper.toResponseList(List.of(itemDealProjection));
        assertThat(actualResponse).containsExactly(itemDealResponse);
    }

    @Test
    void toItemDealsList_returnsCorrectItemDealListResponse_whenValidItemDealProjectionProvided() {
        ItemDealsList expectedResponse = new ItemDealsList(List.of(itemDealResponse));
        ItemDealsList actualResponse = itemDealsMapper.toItemDealsList(List.of(itemDealProjection));
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

}