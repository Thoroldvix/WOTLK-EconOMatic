package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.server.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.thoroldvix.economatic.shared.ValidationUtils.*;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemDealsServiceImpl implements ItemDealsService {

    private final ServerService serverService;
    private final ItemDealsRepository itemDealsRepository;
    private final ItemDealsMapper itemDealsMapper;

    private static void validateInputs(String serverIdentifier, int minQuantity, int minQuality, int limit) {
        notEmpty(serverIdentifier, "Server identifier cannot be null or empty");
        notLessThan(minQuantity, 1, "Minimum quantity must be a positive integer");
        inRange(minQuality, 0, 5, "Min quality cannot be less than 0");
        notLessThan(limit, 1, "Limit must be a positive integer");
    }

    public ItemDealsList getDealsForServer(
            String serverIdentifier,
            int minQuantity,
            int minQuality,
            int limit) {

        validateInputs(serverIdentifier, minQuantity, minQuality, limit);

        List<ItemDealProjection> deals = findDealsForServer(serverIdentifier, minQuantity, minQuality, limit);
        notEmpty(deals,
                () -> new ItemDealsNotFoundException("No deal found for server " + serverIdentifier));

        return itemDealsMapper.toItemDealsList(deals);
    }

    private List<ItemDealProjection> findDealsForServer(String serverIdentifier, int minQuantity, int minQuality, int limit) {
        int serverId = serverService.getServer(serverIdentifier).id();

        return itemDealsRepository.findDealsForServer(serverId, minQuantity, minQuality, limit);
    }
}
