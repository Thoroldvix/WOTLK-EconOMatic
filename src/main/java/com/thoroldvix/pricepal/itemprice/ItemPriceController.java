package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.shared.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/wow-classic/api/v1/items/prices")
@RestController
@RequiredArgsConstructor
public class ItemPriceController {

    private final ItemPriceService itemPriceService;


    @GetMapping("/{serverIdentifier}")
    public ResponseEntity<List<ItemPrice>> getAllRecentForServer(@PathVariable String serverIdentifier) {
       return null;
    }

    @GetMapping("/{serverIdentifier}/{itemIdentifier}")
    public ResponseEntity<ItemPrice> getForServerAndItem(@PathVariable String serverIdentifier,
                                                         @PathVariable String itemIdentifier,
                                                         @RequestParam int timerange) {
        return null;
    }

    @PostMapping("/search")
    public ResponseEntity<List<ItemPrice>> search(@RequestBody RequestDto requestDto) {
        return null;
    }


}
