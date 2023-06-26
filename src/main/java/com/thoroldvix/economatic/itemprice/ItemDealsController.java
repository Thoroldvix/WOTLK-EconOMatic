package com.thoroldvix.economatic.itemprice;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.thoroldvix.economatic.shared.ValidationUtils.hasText;


@RestController
@Tag(name = "Item Deals API", description = "API for retrieving item deals")
@RequestMapping("/wow-classic/api/v1/items/deals")
@RequiredArgsConstructor
public class ItemDealsController {

    private final ItemDealsService itemDealsService;


    @GetMapping("/{serverIdentifier}")
    public ResponseEntity<ItemDealsResponse> getDealsForServer(@PathVariable String serverIdentifier,
                                                               @RequestParam(defaultValue = "4") int limit,
                                                               @RequestParam(defaultValue = "3") int minQuantity,
                                                               @RequestParam(defaultValue = "0") int minQuality) {
        boolean isInvalidInputs = !hasText(serverIdentifier)
                || limit < 1
                || minQuantity < 1
                || minQuality < 0;
        if (isInvalidInputs) {
            return ResponseEntity.badRequest().build();
        }
        var deals = itemDealsService.getDealsForServer(serverIdentifier, minQuantity, minQuality, limit);
        return ResponseEntity.ok(deals);
    }
}
