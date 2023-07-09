package com.thoroldvix.economatic.server;

import com.thoroldvix.economatic.server.dto.ServerListResponse;
import com.thoroldvix.economatic.server.dto.ServerResponse;
import com.thoroldvix.economatic.server.dto.ServerSummaryResponse;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ServerService {

    ServerResponse getServer(String serverIdentifier);

    ServerListResponse search(@Valid SearchRequest searchRequest);

    ServerListResponse getAll();

    ServerSummaryResponse getSummary();

    ServerListResponse getAllForRegion(String regionName);

    ServerListResponse getAllForFaction(String factionName);
}
