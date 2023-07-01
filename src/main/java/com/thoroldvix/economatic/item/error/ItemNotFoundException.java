package com.thoroldvix.economatic.item.error;

import com.thoroldvix.economatic.error.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(String message) {
        super(message);
    }


}
