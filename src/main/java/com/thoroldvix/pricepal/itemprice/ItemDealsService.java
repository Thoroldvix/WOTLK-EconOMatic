package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.server.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.thoroldvix.pricepal.server.ServerErrorMessages.SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY;
import static com.thoroldvix.pricepal.shared.ValidationUtils.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemDealsService {

    private final ServerService serverServiceImpl;
    private final ItemDealsRepository itemDealsRepository;
    private final ItemDealsMapper itemDealsMapper;

    public ItemDealsResponse getDealsForServer(String serverIdentifier, int minQuantity, int minQuality, int limit) {
        validateStringNonNullOrEmpty(serverIdentifier, SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY);
        validatePositiveInt(limit, "Limit must be a positive integer");
        validatePositiveInt(minQuantity, "Minimum quantity must be a positive integer");
        if (minQuality < 0) {
            throw new IllegalArgumentException("Min quality cannot be less than 0");
        }
        List<ItemDealProjection> deals = findDealsForServer(serverIdentifier, minQuantity, minQuality, limit);

        validateCollectionNotNullOrEmpty(deals,
                () -> new ItemDealsNotFoundException("No deals found for server " + serverIdentifier));

        return itemDealsMapper.toDealsWithServer(deals);
    }

    private List<ItemDealProjection> findDealsForServer(String serverIdentifier, int minQuantity, int minQuality, int limit) {

        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return itemDealsRepository.findDealsForServer(serverId, minQuantity, minQuality, limit);
    }
}
