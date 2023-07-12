package com.thoroldvix.economatic.summary.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemSummaryService {

    private final ItemSummaryMapper itemSummaryMapper;
    private final ItemSummaryRepository itemSummaryRepository;

    public ItemSummaryResponse getSummary() {
        ItemSummaryProjection summaryProjection = itemSummaryRepository.getSummary();
        return itemSummaryMapper.toSummaryResponse(summaryProjection);
    }
}
