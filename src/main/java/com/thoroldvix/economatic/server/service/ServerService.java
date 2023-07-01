package com.thoroldvix.economatic.server.service;

import com.thoroldvix.economatic.server.dto.ServerListResponse;
import com.thoroldvix.economatic.server.dto.ServerResponse;
import com.thoroldvix.economatic.server.dto.ServerSummaryResponse;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import static com.thoroldvix.economatic.server.error.ServerErrorMessages.*;

public interface ServerService {

    ServerResponse getServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier);

    ServerListResponse search(@Valid @NotNull(message = "Search request cannot be null") SearchRequest searchRequest);

    ServerListResponse getAll();

    ServerSummaryResponse getSummary();

    ServerListResponse getAllForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName);

    ServerListResponse getAllForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName);
}
