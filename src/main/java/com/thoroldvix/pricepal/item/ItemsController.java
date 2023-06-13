package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.shared.RequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.thoroldvix.pricepal.shared.ValidationUtils.hasText;

@RestController
@Tag(name = "Items API", description = "API for retrieving item information")
@RequestMapping("/wow-classic/v1/items")
@RequiredArgsConstructor
public class ItemsController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems(Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItems(pageable));
    }

    @GetMapping("/itemIdentifier")
    public ResponseEntity<ItemResponse> getItemByItemIdentifier(@RequestParam String itemIdentifier) {
        if (!hasText(itemIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(itemService.getItemByItemIdentifier(itemIdentifier));
    }

    @PostMapping("/search")
    public ResponseEntity<List<ItemResponse>> searchItems(@RequestBody RequestDto requestDto,
                                                          Pageable pageable) {
        return ResponseEntity.ok(itemService.searchItems(requestDto, pageable));
    }
}