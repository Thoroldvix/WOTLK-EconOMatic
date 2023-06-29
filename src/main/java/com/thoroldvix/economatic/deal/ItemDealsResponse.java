package com.thoroldvix.economatic.deal;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.Filters;
import lombok.Builder;

import java.util.List;


@Builder
public record ItemDealsResponse(
        @JsonUnwrapped
        Filters filters,
        List<ItemDealResponse>deals
) {

}
