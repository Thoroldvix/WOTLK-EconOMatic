package com.thoroldvix.economatic.summary.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ItemSummaryServiceImpl implements ItemSummaryService {

    private final ItemSummaryMapper itemSummaryMapper;
    private final ItemSummaryRepository itemSummaryRepository;

    @Override
    public ItemSummaryResponse getSummary() {
        ItemSummaryQualityProjection qualityProjection = itemSummaryRepository.getQualitySummary();
        ItemSummaryTypeProjection typeProjection = itemSummaryRepository.getTypeSummary();
        ItemSummarySlotProjection slotProjection = itemSummaryRepository.getSlotSummary();
        long total = itemSummaryRepository.count();

        return itemSummaryMapper.toSummaryResponse(qualityProjection, typeProjection, slotProjection, total);
    }
}
