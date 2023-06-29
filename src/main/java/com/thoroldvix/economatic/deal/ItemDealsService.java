package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.shared.ServerService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.thoroldvix.economatic.shared.ValidationUtils.validateCollectionNotEmpty;


@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemDealsService {

    private final ServerService serverServiceImpl;
    private final ItemDealsRepository itemDealsRepository;
    private final ItemDealsMapper itemDealsMapper;


    public ItemDealsResponse getDealsForServer(
            @NotEmpty(message = "Server identifier cannot be null or empty")
            String serverIdentifier,
            @Min(value = 1, message = "Minimum quantity must be a positive integer")
            int minQuantity,
            @Min(value = 0, message = "Min quality cannot be less than 0")
            int minQuality,
            @Min(value = 1, message = "Limit must be a positive integer")
            int limit) {
        List<ItemDealProjection> deals = findDealsForServer(serverIdentifier, minQuantity, minQuality, limit);
        validateCollectionNotEmpty(deals,
                () -> new ItemDealsNotFoundException("No deal found for server " + serverIdentifier));
        return itemDealsMapper.toDealsWithServer(deals);
    }

    private List<ItemDealProjection> findDealsForServer(String serverIdentifier, int minQuantity, int minQuality, int limit) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return itemDealsRepository.findDealsForServer(serverId, minQuantity, minQuality, limit);
    }
}
