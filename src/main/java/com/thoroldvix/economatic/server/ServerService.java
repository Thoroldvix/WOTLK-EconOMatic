package com.thoroldvix.economatic.server;

import com.thoroldvix.economatic.search.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ServerService {

    ServerResponse getServer(String serverIdentifier);

    ServerListResponse search(@Valid SearchRequest searchRequest);

    ServerListResponse getAll();

    ServerListResponse getAllForRegion(String regionName);

    ServerListResponse getAllForFaction(String factionName);
}
