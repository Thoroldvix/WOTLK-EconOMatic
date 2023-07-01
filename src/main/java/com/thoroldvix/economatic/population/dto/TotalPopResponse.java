package com.thoroldvix.economatic.population.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;

@Builder
public record TotalPopResponse(
        int popAlliance,
        int popHorde,
        int popTotal,
        @JsonAlias({"name"})
        String serverName
) {
}
