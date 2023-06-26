package com.thoroldvix.economatic.server;

import com.thoroldvix.economatic.shared.SearchRequest;

import java.util.List;

public interface ServerService {

    ServerResponse getServer(String serverIdentifier);

    List<ServerResponse> search(SearchRequest searchRequest);

    List<ServerResponse> getAll();

    List<ServerResponse> getAllForRegion(Region region);

    ServerSummaryResponse getSummary();
}
