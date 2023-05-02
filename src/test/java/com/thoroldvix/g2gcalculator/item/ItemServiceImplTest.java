package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfoList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemsClient itemsClient;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private Map<Integer, Item> itemCache;
    @InjectMocks
    ItemServiceImpl itemServiceImpl;

    @Test
    void getItemByName_whenValidServerNameAndItemName_returnsItemInfo() {
        ItemInfo expectedItemInfo = ItemInfo.builder()
                .itemId(1)
                .name("test")
                .build();
        String serverName = "test";
        String itemName = "test";

        when(itemsClient.getItemByName(serverName, itemName)).thenReturn(expectedItemInfo);

        ItemInfo actualItemInfo = itemServiceImpl.getItemByName(serverName, itemName);

        assertThat(actualItemInfo).isEqualTo(expectedItemInfo);
    }

    @Test
    void getItemByName_whenInvalidServerNameAndValidItemName_throwsIllegalArgumentException() {
        String serverName = "";
        String itemName = "test";


        assertThatThrownBy(() -> itemServiceImpl.getItemByName(serverName, itemName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getItemByName_whenValidServerNameAndInvalidItemName_throwsIllegalArgumentException() {
        String serverName = "test";
        String itemName = "";


        assertThatThrownBy(() -> itemServiceImpl.getItemByName(serverName, itemName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getItemById_whenValidServerNameAndItemId_returnsItemInfo() {
        ItemInfo expectedItemInfo = ItemInfo.builder()
                .itemId(1)
                .name("test")
                .build();
        String serverName = "test";
        int itemId = 13;

        when(itemsClient.getItemById(serverName, itemId)).thenReturn(expectedItemInfo);

        ItemInfo actualItemInfo = itemServiceImpl.getItemById(serverName, itemId);

        assertThat(actualItemInfo).isEqualTo(expectedItemInfo);
    }

    @Test
    void getItemById_whenInvalidServerNameAndValidItemId_throwsIllegalArgumentException() {
        String serverName = "";
        int itemId = 123;


        assertThatThrownBy(() -> itemServiceImpl.getItemById(serverName, itemId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getItemById_whenValidServerNameAndInvalidItemId_throwsIllegalArgumentException() {
        String serverName = "test";
        int itemId = -1;


        assertThatThrownBy(() -> itemServiceImpl.getItemById(serverName, itemId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void saveItem_whenValidItem_savesItem() {
        Item item = Item.builder()
                .id(1)
                .name("test")
                .build();
        itemServiceImpl.saveItem(item);

        verify(itemRepository).save(item);
    }

    @Test
    void getItemCount_returnsCorrectItemCount() {

        long expectedItemCount = 1;
        when(itemRepository.count()).thenReturn(expectedItemCount);

        long actualItemCount = itemServiceImpl.getItemCount();

        assertThat(actualItemCount).isEqualTo(expectedItemCount);
    }

    @Test
    void getAllItemsInfo_whenServerIsCorrect_returnsItemInfoList() {
        itemCache = new HashMap<>();
        String wowheadIcon = "https://wow.zamimg.com/images/wow/icons/large/%s.jpg";
        String serverName = "everlook-alliance";
        ItemInfo basicItemInfo1 = ItemInfo.builder()
                .itemId(1)
                .marketValue(1)
                .minBuyout(2)
                .quantity(3)
                .numAuctions(4)
                .build();
        ItemInfo basicItemInfo2 = ItemInfo.builder()
                .itemId(2)
                .marketValue(5)
                .minBuyout(6)
                .quantity(7)
                .numAuctions(8)
                .build();
        Item item1 = Item.builder()
                .id(1)
                .name("test")
                .icon("test")
                .quality(ItemQuality.EPIC)
                .type(ItemType.GEM)
                .build();
        Item item2 = Item.builder()
                .id(2)
                .name("test2")
                .icon("test2")
                .quality(ItemQuality.RARE)
                .type(ItemType.ARMOR)
                .build();
        ItemInfo itemInfo1 = ItemInfo.builder()
                .itemId(item1.id)
                .marketValue(basicItemInfo1.marketValue())
                .minBuyout(basicItemInfo1.minBuyout())
                .quantity(basicItemInfo1.quantity())
                .numAuctions(basicItemInfo1.numAuctions())
                .name(item1.name)
                .icon(String.format(wowheadIcon, item1.icon))
                .quality(item1.quality)
                .type(item1.type)
                .build();
        ItemInfo itemInfo2 = ItemInfo.builder()
                .itemId(item2.id)
                .marketValue(basicItemInfo2.marketValue())
                .minBuyout(basicItemInfo2.minBuyout())
                .quantity(basicItemInfo2.quantity())
                .numAuctions(basicItemInfo2.numAuctions())
                .name(item2.name)
                .icon(String.format(wowheadIcon, item2.icon))
                .quality(item2.quality)
                .type(item2.type)
                .build();

        List<ItemInfo> expectedResponse = List.of(itemInfo1, itemInfo2);
        List<Item> items = List.of(item1, item2);
        List<ItemInfo> basicItemInfos = List.of(basicItemInfo1, basicItemInfo2);
        Set<Integer> itemIds = Set.of(basicItemInfo1.itemId(), basicItemInfo2.itemId());

        when(itemRepository.findAllById(itemIds)).thenReturn(items);
        when(itemsClient.getAllItems(anyString())).thenReturn(new ItemInfoList(serverName, basicItemInfos));

        List<ItemInfo> actualResponse = itemServiceImpl.getAllItemsInfo(serverName);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getAllItemsInfo_whenServerIsInvalid_throwsIllegalArgumentException() {
        String serverName = "";

        assertThatThrownBy(() -> itemServiceImpl.getAllItemsInfo(serverName))
                .isInstanceOf(IllegalArgumentException.class);
    }
}