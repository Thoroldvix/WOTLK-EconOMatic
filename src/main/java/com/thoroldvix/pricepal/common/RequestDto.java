package com.thoroldvix.pricepal.common;

import lombok.Builder;

import java.util.List;

@Builder
public record RequestDto(
        List<SearchCriteria> searchCriteria,

        GlobalOperator globalOperator
) {
   public enum GlobalOperator {
        AND, OR
    }
    public RequestDto(List<SearchCriteria> searchCriteria) {
        this(searchCriteria, GlobalOperator.AND);
    }
}
