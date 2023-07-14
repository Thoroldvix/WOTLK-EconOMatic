package com.thoroldvix.economatic.dto;

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
