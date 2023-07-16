package com.thoroldvix.economatic.server;

import lombok.Builder;

import java.util.List;

@Builder
public record ServerListResponse(
        List<ServerResponse> servers
) {

}
