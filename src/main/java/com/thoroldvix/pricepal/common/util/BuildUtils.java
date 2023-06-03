package com.thoroldvix.pricepal.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class BuildUtils {
    public Pageable buildPageable(int page, int size, String sort) {
        String sortField = sort.split(",")[0];
        String sortOrder =  sort.split(",")[1];

        Sort.Direction direction = Sort.Direction.DESC;
        if (sortOrder.equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
}
