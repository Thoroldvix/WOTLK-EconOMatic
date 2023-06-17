package com.thoroldvix.pricepal.itemprice;

import com.thoroldvix.pricepal.shared.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/wow-classic/api/v1/items/prices")
@RestController
@RequiredArgsConstructor
public class ItemPriceController {

    private final ItemPriceService itemPriceService;


    @GetMapping("/{serverIdentifier}")
    public ResponseEntity<AuctionHouseInfo> getRecentForServer(@PathVariable String serverIdentifier) {
        AuctionHouseInfo auctionHouseInfo = itemPriceService.getRecentForServer(serverIdentifier);
        return ResponseEntity.ok(auctionHouseInfo);
    }

    @GetMapping("/{serverIdentifier}/{itemIdentifier}")
    public ResponseEntity<AuctionHouseInfo> getRecentForServerAndItem(@PathVariable String serverIdentifier,
                                                                 @PathVariable String itemIdentifier) {
        AuctionHouseInfo auctionHouseInfo = itemPriceService.getRecentForServer(serverIdentifier, itemIdentifier);
        return ResponseEntity.ok(auctionHouseInfo);
    }
     @GetMapping("/{serverIdentifier}/{itemIdentifier}/scans")
    public ResponseEntity<ItemPriceResponse> getRecentForServerAndItem(@PathVariable String serverIdentifier,
                                                                 @PathVariable String itemIdentifier,
                                                                 @RequestParam int timeRange,
                                                                 @ParameterObject Pageable pageable) {
        return null;
    }

    @PostMapping("/search")
    public ResponseEntity<List<ItemPriceResponse>> search(@RequestBody RequestDto requestDto,
                                                          @ParameterObject Pageable pageable) {
        return null;
    }


}
