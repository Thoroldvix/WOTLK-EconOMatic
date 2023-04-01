package com.thoroldvix.g2gcalculator.item;

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
    @InjectMocks
    ItemServiceImpl itemServiceImpl;
    @Mock
    private ItemsClient itemsClient;

    @Test
    void getItemByName_whenValidServerNameAndItemName_returnsItemStats() {
        ItemStats expectedItemStats = ItemStats.builder()
                .itemId(1)
                .name("test")
                .build();
        String serverName = "test";
        String itemName = "test";

        when(itemsClient.getItemByName(serverName, itemName)).thenReturn(expectedItemStats);

        ItemStats actualItemStats = itemServiceImpl.getItemByName(serverName, itemName);

        assertThat(actualItemStats).isEqualTo(expectedItemStats);
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
    void getItemById_whenValidServerNameAndItemId_returnsItemStats() {
        ItemStats expectedItemStats = ItemStats.builder()
                .itemId(1)
                .name("test")
                .build();
        String serverName = "test";
        int itemId = 13;

        when(itemsClient.getItemById(serverName, itemId)).thenReturn(expectedItemStats);

        ItemStats actualItemStats = itemServiceImpl.getItemById(serverName, itemId);

        assertThat(actualItemStats).isEqualTo(expectedItemStats);
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