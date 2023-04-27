package com.thoroldvix.g2gcalculator.item;

import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemsClient itemsClient;

    @Mock
    private ItemRepository itemRepository;
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
    }