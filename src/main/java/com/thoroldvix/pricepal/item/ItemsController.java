package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.shared.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/v1/items")
@RequiredArgsConstructor
public class ItemsController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemInfo>> getAllItems(Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItems(pageable));
    }

    @PostMapping("/search")
    public ResponseEntity<List<ItemInfo>> searchItems(@RequestBody RequestDto requestDto,
                                                      Pageable pageable) {
        return ResponseEntity.ok(itemService.searchItems(requestDto, pageable));
    }
}