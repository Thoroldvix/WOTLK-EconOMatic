package com.thoroldvix.economatic.summary.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ServerSummaryServiceImpl implements ServerSummaryService {

    private final ServerSummaryRepository serverRepository;
    private final ServerSummaryMapper serverSummaryMapper;

    public ServerSummaryResponse getSummary() {
        ServerSummaryProjection summaryProjection = serverRepository.getSummary();
        return serverSummaryMapper.toResponse(summaryProjection);
    }
}
