package com.thoroldvix.economatic.server;

import com.thoroldvix.economatic.shared.SearchRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import static com.thoroldvix.economatic.server.ServerErrorMessages.*;

public interface ServerService {

    ServerResponse getServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier);

    List<ServerResponse> search(@Valid @NotNull(message = "Search request cannot be null") SearchRequest searchRequest);

    List<ServerResponse> getAll();

    ServerSummaryResponse getSummary();

    List<ServerResponse> getAllForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName);

    List<ServerResponse> getAllForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName);
}
