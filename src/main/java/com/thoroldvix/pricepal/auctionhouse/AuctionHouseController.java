package com.thoroldvix.pricepal.auctionhouse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.thoroldvix.pricepal.shared.ValidationUtils.hasText;

@RequestMapping("/wow-classic/api/v1/auction-house")
@RestController
@RequiredArgsConstructor
public class AuctionHouseController {

    private final AuctionHouseService auctionHouseService;

}
