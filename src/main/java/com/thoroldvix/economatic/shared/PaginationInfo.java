package com.thoroldvix.economatic.shared;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PaginationInfo(
        int page,
        int pageSize,
        int totalPages,
        long totalElements
) {
    public PaginationInfo(Page<?> page) {
        this(page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements());
    }
}
