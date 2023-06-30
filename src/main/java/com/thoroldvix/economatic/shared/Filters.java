package com.thoroldvix.economatic.shared;

import lombok.Builder;

@Builder
public record Filters(
        String server,
        String region,
        String faction
) {

}
