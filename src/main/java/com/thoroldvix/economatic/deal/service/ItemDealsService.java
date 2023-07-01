package com.thoroldvix.economatic.deal.service;

import com.thoroldvix.economatic.deal.dto.ItemDealsList;
import com.thoroldvix.economatic.deal.error.ItemDealsNotFoundException;
import com.thoroldvix.economatic.deal.mapper.ItemDealsMapper;
import com.thoroldvix.economatic.deal.repository.ItemDealProjection;
import com.thoroldvix.economatic.deal.repository.ItemDealsRepository;
import com.thoroldvix.economatic.server.service.ServerService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.thoroldvix.economatic.shared.util.Utils.notEmpty;


@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemDealsService {

    private final ServerService serverServiceImpl;
    private final ItemDealsRepository itemDealsRepository;
    private final ItemDealsMapper itemDealsMapper;


    public ItemDealsList getDealsForServer(
            @NotEmpty(message = "Server identifier cannot be null or empty")
            String serverIdentifier,
            @Min(value = 1, message = "Minimum quantity must be a positive integer")
            int minQuantity,
            @Min(value = 0, message = "Min quality cannot be less than 0")
            int minQuality,
            @Min(value = 1, message = "Limit must be a positive integer")
            int limit) {
        List<ItemDealProjection> deals = findDealsForServer(serverIdentifier, minQuantity, minQuality, limit);
        notEmpty(deals,
                () -> new ItemDealsNotFoundException("No deal found for server " + serverIdentifier));
        return itemDealsMapper.toItemDealsList(deals);
    }

    private List<ItemDealProjection> findDealsForServer(String serverIdentifier, int minQuantity, int minQuality, int limit) {
        int serverId = serverServiceImpl.getServer(serverIdentifier).id();
        return itemDealsRepository.findDealsForServer(serverId, minQuantity, minQuality, limit);
    }
}
