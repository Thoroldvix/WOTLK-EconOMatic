package com.thoroldvix.economatic.server.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ServerListResponse(
        List<ServerResponse> servers
) {
}
