package com.thoroldvix.economatic.deal;

import com.thoroldvix.economatic.server.ServerResponse;
import com.thoroldvix.economatic.server.ServerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static com.thoroldvix.economatic.common.util.ValidationUtils.notEmpty;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ItemDealsServiceImpl implements ItemDealsService {

    private final ServerService serverService;
    private final ItemDealsRepository itemDealsRepository;
    private final ItemDealsMapper itemDealsMapper;

    public ItemDealsList getDealsForServer(@Valid ItemDealsRequest request) {
        Objects.requireNonNull(request, "Item deals request cannot be null or empty");

        ServerResponse server = serverService.getServer(request.serverIdentifier());
        List<ItemDealProjection> deals = findDealsForServer(request, server);

        notEmpty(deals,
                () -> new ItemDealsNotFoundException("No deal found for server " + request.serverIdentifier()));

        return itemDealsMapper.toItemDealsList(deals);
    }

    private List<ItemDealProjection> findDealsForServer(ItemDealsRequest request, ServerResponse server) {
        return itemDealsRepository.findDealsForServer(server.id(), request.minQuantity(), request.minQuality(), request.limit());
    }
}
