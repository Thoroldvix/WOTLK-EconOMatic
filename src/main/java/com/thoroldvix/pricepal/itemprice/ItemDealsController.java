package com.thoroldvix.pricepal.itemprice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/api/v1/items/deals")
@RequiredArgsConstructor
public class ItemDealsController {
    private final ItemDealsService itemDealsService;


    @GetMapping("/{serverIdentifier}")
    public ResponseEntity<List<ItemDealsResponse>> getDealsForServer(@PathVariable String serverIdentifier) {
        return null;
    }

}
