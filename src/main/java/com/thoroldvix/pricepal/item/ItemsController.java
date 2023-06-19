package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.shared.RequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.thoroldvix.pricepal.shared.ValidationUtils.hasText;

@RestController
@Tag(name = "Items API", description = "API for retrieving item information")
@RequestMapping("/wow-classic/api/v1/items")
@Validated
@RequiredArgsConstructor
public class ItemsController {

    private final ItemServiceImpl itemServiceImpl;

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(itemServiceImpl.getAll(pageable));
    }

    @GetMapping("/itemIdentifier")
    public ResponseEntity<ItemResponse> getItem(@RequestParam String itemIdentifier) {
        if (!hasText(itemIdentifier)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(itemServiceImpl.getItem(itemIdentifier));
    }

    @PostMapping("/search")
    public ResponseEntity<List<ItemResponse>> search(@RequestBody  RequestDto requestDto,
                                                     Pageable pageable) {
        return ResponseEntity.ok(itemServiceImpl.search(requestDto, pageable));
    }
    @GetMapping("/summary")
    public ResponseEntity<Map<String,  ItemSummaryResponse>> getItemSummary() {
        ItemSummaryResponse itemSummaryResponse = itemServiceImpl.getSummary();
        return ResponseEntity.ok(Map.of("summary", itemSummaryResponse));
    }
    @PostMapping
    public ResponseEntity<ItemResponse> addItem(@RequestBody @Valid ItemRequest itemRequest) {
        ItemResponse itemResponse = itemServiceImpl.addItem(itemRequest);
        return ResponseEntity.ok(itemResponse);
    }
}