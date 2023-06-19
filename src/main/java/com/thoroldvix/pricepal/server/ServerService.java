package com.thoroldvix.pricepal.server;

import com.thoroldvix.pricepal.shared.RequestDto;

import java.util.List;

public interface ServerService {

    ServerResponse getServer(String serverIdentifier);

    List<ServerResponse> search(RequestDto requestDto);

    List<ServerResponse> getAll();

    List<ServerResponse> getAllForRegion(Region region);

    ServerSummaryResponse getSummary();
}
